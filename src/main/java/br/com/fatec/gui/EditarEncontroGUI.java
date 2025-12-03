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
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Optional;

public class EditarEncontroGUI extends JFrame {

    private JFormattedTextField txtDataEncontro;
    private JButton btnAtualizar;

    private JComboBox<Mae>[] cbMaes;
    private List<Mae> listaMaes;
    private Map<String, ServicoFixo> mapServicosFixos;
    private JTextField[] txtDescricoes;

    private Encontro encontroOriginal;
    private int idEncontro;

    private Map<String, Integer> mapServicoEncontroIdOriginal;

    private final String[] SERVICOS_FIXOS = {
            "MÚSICA", "RECEPÇÃO DE MÃES", "ACOLHIDA", "TERÇO",
            "FORMAÇÃO", "MOMENTO ORACIONAL", "PROCLAMAÇÃO DA VITÓRIA",
            "SORTEIO DAS FLORES", "ENCERRAMENTO", "ARRUMAÇÃO CAPELA",
            "QUEIMA DOS PEDIDOS", "COMPRAS FLORES"
    };

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final int LARGURA_JANELA = 650;
    private final int ALTURA_JANELA = 550;

    public EditarEncontroGUI(int idEncontro) {
        super("Editar Encontro e Escala (ID: " + idEncontro + ")");
        this.idEncontro = idEncontro;

        carregarDados();

        if (encontroOriginal == null) {
            SwingUtilities.invokeLater(() -> this.dispose());
            return;
        }

        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        cbMaes = new JComboBox[SERVICOS_FIXOS.length];
        txtDescricoes = new JTextField[SERVICOS_FIXOS.length];

        JPanel panelData = criarPainelData();
        add(panelData, BorderLayout.NORTH);

        JScrollPane scrollPane = criarPainelEscala();
        add(scrollPane, BorderLayout.CENTER);

        btnAtualizar = new JButton("Atualizar Encontro e Escala");
        btnAtualizar.addActionListener(e -> atualizarEncontro());
        JPanel panelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelRodape.add(btnAtualizar);
        add(panelRodape, BorderLayout.SOUTH);

        preencherDados();

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setVisible(true);
    }

    private void carregarDados() {
        try {
            EncontroDAO encontroDAO = new EncontroDAO();
            MaeDAO maeDAO = new MaeDAO();
            ServicoFixoDAO servicoFixoDAO = new ServicoFixoDAO();

            encontroOriginal = encontroDAO.buscarComEscalaPorId(this.idEncontro);

            if (encontroOriginal == null) {
                JOptionPane.showMessageDialog(null, "Encontro ID " + this.idEncontro + " não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            listaMaes = maeDAO.listarTodas();

            mapServicosFixos = servicoFixoDAO.listarTodos().stream()
                    .collect(Collectors.toMap(ServicoFixo::getNome, Function.identity()));

            mapServicoEncontroIdOriginal = encontroOriginal.getServicos().stream()
                    .collect(Collectors.toMap(
                            s -> s.getServicoFixo().getNome(),
                            ServicoEncontro::getId
                    ));

            Mae naoSelecionada = new Mae("Não Selecionada", null, null, null);
            naoSelecionada.setId(0);
            listaMaes.add(0, naoSelecionada);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados para edição: " + e.getMessage(), "Erro de SQL", JOptionPane.ERROR_MESSAGE);
            listaMaes = new ArrayList<>();
            encontroOriginal = null;
            e.printStackTrace();
        }
    }

    private void preencherDados() {
        if (encontroOriginal == null) return;

        txtDataEncontro.setText(encontroOriginal.getDataEncontro().format(DATE_FORMATTER));

        List<ServicoEncontro> servicosDoEncontro = encontroOriginal.getServicos();

        for (int i = 0; i < SERVICOS_FIXOS.length; i++) {
            final String tipoServicoFixo = SERVICOS_FIXOS[i];

            Optional<ServicoEncontro> servicoEncontradoOpt = servicosDoEncontro.stream()
                    .filter(s -> s.getServicoFixo().getNome().equalsIgnoreCase(tipoServicoFixo))
                    .findFirst();

            if (servicoEncontradoOpt.isPresent()) {
                ServicoEncontro servicoEncontrado = servicoEncontradoOpt.get();

                txtDescricoes[i].setText(servicoEncontrado.getDescricaoAtividade());

                Mae maeResp = servicoEncontrado.getMaeResponsavel();
                if (maeResp != null) {
                    for (int j = 0; j < cbMaes[i].getItemCount(); j++) {
                        Mae itemMae = cbMaes[i].getItemAt(j);
                        if (itemMae != null && itemMae.getId() == maeResp.getId()) {
                            cbMaes[i].setSelectedIndex(j);
                            break;
                        }
                    }
                } else {
                    cbMaes[i].setSelectedIndex(0);
                }
            } else {
                cbMaes[i].setSelectedIndex(0);
                txtDescricoes[i].setText("");
            }
        }
    }

    private JPanel criarPainelData() {
        JPanel panelData = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String titulo = "Informações do Encontro (ID: " + encontroOriginal.getId() + ")";
        panelData.setBorder(BorderFactory.createTitledBorder(titulo));
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

    private JScrollPane criarPainelEscala() {
        JPanel panelEscala = new JPanel(new GridBagLayout());
        panelEscala.setBorder(BorderFactory.createTitledBorder("Escala de Serviços"));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.gridx = 0;
        panelEscala.add(new JLabel("Serviço"), gbc);
        gbc.gridx = 1;
        panelEscala.add(new JLabel("Mãe Responsável"), gbc);
        gbc.gridx = 2;
        panelEscala.add(new JLabel("Descrição (Opcional)"), gbc);

        for (int i = 0; i < SERVICOS_FIXOS.length; i++) {
            gbc.gridy = i + 1;

            gbc.gridx = 0;
            panelEscala.add(new JLabel(SERVICOS_FIXOS[i] + ":"), gbc);

            gbc.gridx = 1;
            JComboBox<Mae> cbMae = new JComboBox<>();
            for (Mae mae : listaMaes) {
                cbMae.addItem(mae);
            }
            cbMaes[i] = cbMae;
            panelEscala.add(cbMae, gbc);

            gbc.gridx = 2;
            JTextField txtDescricao = new JTextField(15);
            txtDescricoes[i] = txtDescricao;
            panelEscala.add(txtDescricao, gbc);
        }

        gbc.gridy = SERVICOS_FIXOS.length + 1;
        gbc.weighty = 1.0;
        panelEscala.add(new JLabel(""), gbc);

        return new JScrollPane(panelEscala);
    }

    private void atualizarEncontro() {
        String dataStr = txtDataEncontro.getText().trim();
        LocalDate novaData;

        try {
            novaData = LocalDate.parse(dataStr, DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use o padrão DD/MM/AAAA.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Encontro encontroAtualizado = new Encontro(novaData);
        encontroAtualizado.setId(encontroOriginal.getId());
        encontroAtualizado.setExcluidoLogico(encontroOriginal.isExcluidoLogico());

        List<ServicoEncontro> novosServicos = new ArrayList<>();

        for (int i = 0; i < SERVICOS_FIXOS.length; i++) {
            Mae maeSelecionada = (Mae) cbMaes[i].getSelectedItem();
            String descricao = txtDescricoes[i].getText().trim();
            String tipoServicoNome = SERVICOS_FIXOS[i];

            int idServicoEncontroOriginal = mapServicoEncontroIdOriginal.getOrDefault(tipoServicoNome, -1);

            ServicoFixo servicoFixo = mapServicosFixos.get(tipoServicoNome);

            if (servicoFixo == null) {
                JOptionPane.showMessageDialog(this, "Erro: Serviço fixo '" + tipoServicoNome + "' não encontrado no banco.", "Erro de Configuração", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Mae maeResponsavel = null;
            if (maeSelecionada != null && maeSelecionada.getId() > 0) {
                maeResponsavel = maeSelecionada;
            }

            ServicoEncontro servico = new ServicoEncontro(
                    idServicoEncontroOriginal,
                    encontroAtualizado,
                    servicoFixo,
                    maeResponsavel,
                    descricao
            );
            novosServicos.add(servico);
        }
        encontroAtualizado.setServicos(novosServicos);

        try {
            EncontroDAO dao = new EncontroDAO();
            dao.atualizar(encontroAtualizado);

            JOptionPane.showMessageDialog(this, "Encontro ID " + encontroOriginal.getId() + " atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar Encontro: " + ex.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}