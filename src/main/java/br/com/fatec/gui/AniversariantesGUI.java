package br.com.fatec.gui;

import br.com.fatec.dao.MaeDAO;
import br.com.fatec.modelo.Mae;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.YearMonth; // Usado para rastrear o mês e ano
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;

public class AniversariantesGUI extends JFrame {

    private JTable tabelaAniversariantes;
    private DefaultTableModel tableModel;
    private JLabel lblMesAtual;
    private YearMonth mesAnoVisualizado;
    private final int LARGURA_JANELA = 550;
    private final int ALTURA_JANELA = 400;

    public AniversariantesGUI() {
        super("Visualizar Aniversariantes");

        mesAnoVisualizado = YearMonth.now();

        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"Nome da Mãe", "Data de Aniversário"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaAniversariantes = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaAniversariantes);

        JPanel painelControle = criarPainelControle();

        add(painelControle, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        atualizarTela();

        setVisible(true);
    }

    private JPanel criarPainelControle() {
        JPanel painel = new JPanel(new BorderLayout(15, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painel.setBackground(new Color(240, 240, 240));

        JButton btnAnterior = new JButton("<< Anterior");
        JButton btnProximo = new JButton("Próximo >>");

        btnAnterior.addActionListener(e -> mudarMes(-1));
        btnProximo.addActionListener(e -> mudarMes(1));

        lblMesAtual = new JLabel("", JLabel.CENTER);
        lblMesAtual.setFont(new Font("Arial", Font.BOLD, 18));
        lblMesAtual.setForeground(new Color(50, 70, 100));

        painel.add(btnAnterior, BorderLayout.WEST);
        painel.add(lblMesAtual, BorderLayout.CENTER);
        painel.add(btnProximo, BorderLayout.EAST);

        return painel;
    }

    private void mudarMes(int delta) {
        mesAnoVisualizado = mesAnoVisualizado.plusMonths(delta);
        atualizarTela();
    }

    private void atualizarTela() {
        String nomeMes = mesAnoVisualizado.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        String tituloFormatado = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1).toLowerCase()
                + " de " + mesAnoVisualizado.getYear();

        lblMesAtual.setText(tituloFormatado);

        carregarAniversariantes();
    }

    private void carregarAniversariantes() {
        tableModel.setRowCount(0);

        MaeDAO dao = new MaeDAO();

        int mesConsulta = mesAnoVisualizado.getMonthValue();

        try {
            List<Mae> aniversariantes = dao.listarAniversariantesDoMes(mesConsulta);

            if (aniversariantes.isEmpty()) {
                tableModel.addRow(new Object[]{"Nenhuma mãe aniversariante neste mês.", ""});
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

                for (Mae mae : aniversariantes) {
                    tableModel.addRow(new Object[]{
                            mae.getNome(),
                            mae.getDataAniversario().format(formatter)
                    });
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar lista de aniversariantes: " + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}