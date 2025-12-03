package br.com.fatec.gui;

import br.com.fatec.dao.EncontroDAO;
import br.com.fatec.dao.MaeDAO;
import br.com.fatec.dao.ServicoFixoDAO;
import br.com.fatec.modelo.Encontro;
import br.com.fatec.modelo.Mae;
import br.com.fatec.modelo.ServicoEncontro;
import br.com.fatec.modelo.ServicoFixo;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AgendarEncontroGUI extends JFrame {

    private JFormattedTextField txtDataEncontro;
    private JScrollPane scrollPaneServicos;
    private JPanel panelServicosDinamico;

    private List<ServicoFixo> listaServicosFixos;
    private List<Mae> listaMaes;

    private List<JComboBox<Mae>> comboMaesEscaladas;
    private List<JTextField> txtDescricoes;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Mae NAO_ESCALADO = new Mae(0, "— Não Escalado —");

    public AgendarEncontroGUI() {
        super("Agendar Novo Encontro");

        comboMaesEscaladas = new ArrayList<>();
        txtDescricoes = new ArrayList<>();
        carregarDadosBase();

        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelData(), BorderLayout.NORTH);
        add(criarPainelServicos(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setVisible(true);
    }

    private void carregarDadosBase() {
        try {
            MaeDAO maeDao = new MaeDAO();
            ServicoFixoDAO servicoDao = new ServicoFixoDAO();

            listaMaes = maeDao.listarTodos();
            listaServicosFixos = servicoDao.listarTodos();

            listaMaes.add(0, NAO_ESCALADO);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar Mães ou Serviços Fixos: " + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            listaMaes = new ArrayList<>();
            listaServicosFixos = new ArrayList<>();
        }
    }

    private JPanel criarPainelData() {
        JPanel panelData = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelData.setBorder(BorderFactory.createTitledBorder("Informações do Encontro"));
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataEncontro = new JFormattedTextField(dateMask);
            txtDataEncontro.setPreferredSize(new Dimension(100, 25));
        } catch (ParseException e) {
            txtDataEncontro = new JFormattedTextField();
            e.printStackTrace();
        }

        panelData.add(new JLabel("Data do Encontro (DD/MM/AAAA):"));
        panelData.add(txtDataEncontro);
        return panelData;
    }

    private JScrollPane criarPainelServicos() {
        panelServicosDinamico = new JPanel(new GridLayout(0, 3, 5, 5));
        panelServicosDinamico.setBorder(BorderFactory.createTitledBorder("Escala de Serviços"));

        panelServicosDinamico.add(new JLabel("Serviço:", JLabel.CENTER));
        panelServicosDinamico.add(new JLabel("Mãe Responsável:", JLabel.CENTER));
        panelServicosDinamico.add(new JLabel("Descrição da Atividade:", JLabel.CENTER));

        for (ServicoFixo servico : listaServicosFixos) {

            panelServicosDinamico.add(new JLabel(servico.getNome() + ":", JLabel.LEFT));

            JComboBox<Mae> comboMae = new JComboBox<>();

            DefaultComboBoxModel<Mae> model = new DefaultComboBoxModel<>();
            for (Mae mae : listaMaes) {
                model.addElement(mae);
            }
            comboMae.setModel(model);

            comboMae.setSelectedItem(NAO_ESCALADO);

            comboMae.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Mae) {
                        setText(((Mae) value).getNome());
                    }
                    return this;
                }
            });

            panelServicosDinamico.add(comboMae);
            comboMaesEscaladas.add(comboMae);

            JTextField txtDescricao = new JTextField(20);
            panelServicosDinamico.add(txtDescricao);
            txtDescricoes.add(txtDescricao);
        }

        scrollPaneServicos = new JScrollPane(panelServicosDinamico);
        scrollPaneServicos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneServicos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPaneServicos;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnSalvar = new JButton("Salvar Agendamento");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvarEncontro());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnSalvar);
        panel.add(btnCancelar);

        return panel;
    }

    private void salvarEncontro() {
        String dataStr = txtDataEncontro.getText().trim();
        LocalDate dataEncontro;

        try {
            if (dataStr.contains("_") || dataStr.replace("/", "").isEmpty()) {
                JOptionPane.showMessageDialog(this, "A data do encontro deve ser preenchida.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dataEncontro = LocalDate.parse(dataStr, DATE_FORMATTER);

            if (dataEncontro.isBefore(LocalDate.now())) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "A data do encontro é no passado. Deseja prosseguir?",
                        "Confirmação de Data", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.NO_OPTION) return;
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use DD/MM/AAAA.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Encontro encontro = new Encontro(dataEncontro, false); // Novo encontro não está excluído

        List<ServicoEncontro> servicosEscalados = new ArrayList<>();

        for (int i = 0; i < listaServicosFixos.size(); i++) {

            ServicoFixo servicoFixo = listaServicosFixos.get(i);
            JComboBox<Mae> combo = comboMaesEscaladas.get(i);
            JTextField txtDescricao = txtDescricoes.get(i);

            Mae maeSelecionada = (Mae) combo.getSelectedItem();
            String descricao = txtDescricao.getText().trim();

            if (maeSelecionada.getId() != 0 || !descricao.isEmpty()) {

                Mae mae = (maeSelecionada.getId() != 0) ? maeSelecionada : null; // Define Mae como null se for "Não Escalado"

                ServicoEncontro se = new ServicoEncontro(
                        encontro,
                        servicoFixo,
                        mae,
                        descricao
                );
                servicosEscalados.add(se);
            }
        }

        encontro.setServicos(servicosEscalados);

        if (servicosEscalados.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Nenhum serviço foi escalado para este encontro. Deseja salvar o encontro vazio?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.NO_OPTION) return;
        }


        try {
            EncontroDAO dao = new EncontroDAO();
            dao.salvarComEscala(encontro);

            JOptionPane.showMessageDialog(this, "Encontro agendado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a janela após o sucesso

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao agendar o encontro: " + e.getMessage(),
                    "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}