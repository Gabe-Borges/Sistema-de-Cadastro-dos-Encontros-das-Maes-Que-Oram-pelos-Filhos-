package br.com.fatec.gui;

import br.com.fatec.dao.EncontroDAO;
import br.com.fatec.modelo.Encontro;
import br.com.fatec.modelo.ServicoEncontro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class VisualizarEncontrosGUI extends JFrame {

    private JTable tabelaEncontros;
    private DefaultTableModel modeloTabela;
    private List<Encontro> encontros;
    private EncontroDAO dao;

    private final DateTimeFormatter DATE_FORMATTER_TABELA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter DATE_FORMATTER_ARQUIVO = DateTimeFormatter.ofPattern("ddMMyyyy");
    private final DateTimeFormatter DATE_FORMATTER_RELATORIO = DateTimeFormatter.ofPattern("dd/MM");

    public VisualizarEncontrosGUI() {
        super("Visualizar e Gerenciar Encontros");

        dao = new EncontroDAO();

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Data do Encontro", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEncontros = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaEncontros);
        add(scrollPane, BorderLayout.CENTER);

        add(criarPainelAcoes(), BorderLayout.SOUTH);

        carregarTabela();

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setVisible(true);
    }

    private JPanel criarPainelAcoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnEditar = new JButton("Editar Encontro");
        JButton btnRelatorio = new JButton("Gerar Relatório (.txt)");
        JButton btnDeletar = new JButton("Cancelar Encontro");

        btnRelatorio.setBackground(new Color(220, 240, 255));
        btnDeletar.setBackground(new Color(255, 220, 220));

        btnEditar.addActionListener(e -> editarEncontroSelecionado());
        btnRelatorio.addActionListener(e -> gerarRelatorioSelecionado());
        btnDeletar.addActionListener(e -> deletarEncontroSelecionado());

        panel.add(btnEditar);
        panel.add(btnRelatorio);
        panel.add(btnDeletar);

        return panel;
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            encontros = dao.listarTodos();
            for (Encontro encontro : encontros) {
                Object[] linha = {
                        encontro.getId(),
                        encontro.getDataEncontro().format(DATE_FORMATTER_TABELA),
                        encontro.getStatus()
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Encontro getEncontroSelecionadoNaTabela() {
        int linhaSelecionada = tabelaEncontros.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um encontro na tabela primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return encontros.get(linhaSelecionada);
    }

    private void editarEncontroSelecionado() {
        Encontro encontro = getEncontroSelecionadoNaTabela();
        if (encontro != null) {

            if (encontro.getStatus().equals("CANCELADO")) {
                JOptionPane.showMessageDialog(this, "Não é possível editar um encontro cancelado. Para reverter, cancele o cancelamento primeiro (não implementado).", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            EditarEncontroGUI editor = new EditarEncontroGUI(encontro.getId());

            editor.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    carregarTabela();
                }
            });
            editor.setVisible(true);
        }
    }

    private void gerarRelatorioSelecionado() {
        Encontro encontroSimples = getEncontroSelecionadoNaTabela();
        if (encontroSimples == null) return;

        if (encontroSimples.getStatus().equals("CANCELADO")) {
            JOptionPane.showMessageDialog(this, "Não é possível gerar relatório de um encontro cancelado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Encontro encontroCompleto = dao.buscarComEscalaPorId(encontroSimples.getId());

            if (encontroCompleto != null) {
                escreverArquivoTxt(encontroCompleto);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao buscar detalhes do encontro ID " + encontroSimples.getId(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro de Banco de Dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void escreverArquivoTxt(Encontro encontro) {
        String nomeArquivoPadrao = "Relatorio_" + encontro.getDataEncontro().format(DATE_FORMATTER_ARQUIVO) + ".txt";

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setSelectedFile(new java.io.File(nomeArquivoPadrao));

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {

            java.io.File arquivoDestino = fileChooser.getSelectedFile();

            String caminhoFinal = arquivoDestino.getAbsolutePath();
            if (!caminhoFinal.toLowerCase().endsWith(".txt")) {
                caminhoFinal += ".txt";
            }

            try (FileWriter writer = new FileWriter(caminhoFinal)) {

                writer.write("Relatório do Encontro: " + encontro.getDataEncontro().format(DATE_FORMATTER_RELATORIO) + "\n");
                writer.write("--------------------------------------------------------------------\n");
                writer.write("Status do Encontro: " + (encontro.isExcluidoLogico() ? "CANCELADO" : "ATIVO") + "\n\n");

                writer.write("ESCALA DE SERVIÇOS:\n");

                List<ServicoEncontro> servicos = encontro.getServicos();

                servicos.sort(Comparator.comparing(se -> se.getServicoFixo().getId()));

                for (ServicoEncontro se : servicos) {
                    String nomeServico = se.getServicoFixo().getNome();
                    String nomeMae = (se.getMaeResponsavel() != null) ? se.getMaeResponsavel().getNome() : "NÃO ESCALADO";
                    String descricao = se.getDescricaoAtividade().isEmpty() ? "" : " - Descrição: " + se.getDescricaoAtividade();

                    writer.write(String.format("%-25s %s%s\n", nomeServico + ":", nomeMae, descricao));
                }

                JOptionPane.showMessageDialog(this,
                        "Relatório gerado com sucesso!\nSalvo em: " + caminhoFinal,
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao gravar arquivo:\n" + e.getMessage(),
                        "Erro de I/O", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletarEncontroSelecionado() {
        Encontro encontro = getEncontroSelecionadoNaTabela();
        if (encontro == null) return;

        if (encontro.getStatus().equals("CANCELADO")) {
            JOptionPane.showMessageDialog(this, "Este encontro já está cancelado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente CANCELAR o encontro do dia " + encontro.getDataEncontro().format(DATE_FORMATTER_TABELA) + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.excluirLogico(encontro.getId());
                carregarTabela();
                JOptionPane.showMessageDialog(this, "Encontro cancelado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}