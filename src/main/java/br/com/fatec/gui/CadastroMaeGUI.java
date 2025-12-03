package br.com.fatec.gui;

import br.com.fatec.dao.MaeDAO;
import br.com.fatec.modelo.Mae;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CadastroMaeGUI extends JFrame {

    private JTextField txtNome;
    private JFormattedTextField txtTelefone;
    private JTextField txtEndereco;

    private JFormattedTextField txtDataAniversario;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CadastroMaeGUI() {
        super("Cadastro de Mãe");

        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelEntradas = criarPainelEntradas();
        JPanel panelNascimento = criarPainelNascimento();
        JPanel panelBotoes = criarPainelBotoes();

        JPanel panelDados = new JPanel(new GridLayout(2, 1, 10, 10));
        panelDados.add(panelEntradas);
        panelDados.add(panelNascimento);

        add(panelDados, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setVisible(true);
    }

    private JPanel criarPainelNascimento() {
        JPanel panelNascimento = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNascimento.setBorder(BorderFactory.createTitledBorder("Dados de Nascimento"));

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataAniversario = new JFormattedTextField(dateMask);
            txtDataAniversario.setPreferredSize(new Dimension(100, 25));
        } catch (ParseException e) {
            txtDataAniversario = new JFormattedTextField();
            e.printStackTrace();
        }

        panelNascimento.add(new JLabel("Data de Aniversário (DD/MM/AAAA):"));
        panelNascimento.add(txtDataAniversario);

        return panelNascimento;
    }
    private JPanel criarPainelEntradas() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Informações Pessoais"));

        txtNome = new JTextField(30);
        txtEndereco = new JTextField(30);

        try {
            // Máscara para Telefone (ex: (12) 99999-9999)
            MaskFormatter phoneMask = new MaskFormatter("(##) #####-####");
            phoneMask.setPlaceholderCharacter('_');
            txtTelefone = new JFormattedTextField(phoneMask);
        } catch (ParseException e) {
            txtTelefone = new JFormattedTextField();
            e.printStackTrace();
        }

        panel.add(new JLabel("Nome da Mãe:"));
        panel.add(txtNome);

        panel.add(new JLabel("Telefone:"));
        panel.add(txtTelefone);

        panel.add(new JLabel("Endereço:"));
        panel.add(txtEndereco);

        return panel;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvarMae());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnSalvar);
        panel.add(btnCancelar);

        return panel;
    }

    private void salvarMae() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().replaceAll("[^0-9]", "");
        String endereco = txtEndereco.getText().trim();
        String dataAniversarioStr = txtDataAniversario.getText().trim();
        LocalDate dataAniversario;

        if (nome.isEmpty() || endereco.isEmpty() || telefone.length() < 10 || dataAniversarioStr.contains("_")) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente (Nome, Endereço, Telefone e Data de Aniversário).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            dataAniversario = LocalDate.parse(dataAniversarioStr, DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data de Aniversário inválida. Use o formato DD/MM/AAAA.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Mae mae = new Mae(nome, telefone, endereco, dataAniversario);
        MaeDAO dao = new MaeDAO();

        try {
            dao.salvar(mae);
            JOptionPane.showMessageDialog(this, "Mãe cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            txtNome.setText("");
            txtTelefone.setValue(null);
            txtEndereco.setText("");
            txtDataAniversario.setValue(null);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar no banco de dados: " + e.getMessage(),
                    "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}