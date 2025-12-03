package br.com.fatec.gui;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private final Dimension BUTTON_SIZE = new Dimension(300, 45);
    private final Font BUTTON_FONT = new Font("SansSerif", Font.PLAIN, 15);
    private final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private final Color BUTTON_BASE_COLOR = new Color(230, 235, 245);
    private final Color HEADER_COLOR = new Color(50, 70, 100);

    public MenuPrincipal() {
        super("Sistema Mães Que Oram");

        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // --- 1. HEADER ---
        JLabel lblTitulo = new JLabel("MÃES QUE ORAM PELOS FILHOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(HEADER_COLOR);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Organização de Encontros e Escala");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(30));
        add(lblTitulo);
        add(Box.createVerticalStrut(5));
        add(lblSubtitulo);
        add(Box.createVerticalStrut(30));

        JButton btnCadastroMae = criarBotaoMenu("1. Cadastrar Mães");
        JButton btnAgendarEncontro = criarBotaoMenu("2. Agendar Encontro e Escala");
        JButton btnVisualizarEncontros = criarBotaoMenu("3. Visualizar e Gerenciar Encontros");
        JButton btnVerAniversariantes = criarBotaoMenu("4. Ver Aniversariantes do Mês");

        add(btnCadastroMae);
        add(Box.createVerticalStrut(12));
        add(btnAgendarEncontro);
        add(Box.createVerticalStrut(12));
        add(btnVisualizarEncontros);
        add(Box.createVerticalStrut(12));
        add(btnVerAniversariantes);

        add(Box.createVerticalStrut(30));

        btnCadastroMae.addActionListener(e -> new CadastroMaeGUI().setVisible(true));
        btnAgendarEncontro.addActionListener(e -> new AgendarEncontroGUI().setVisible(true));
        btnVisualizarEncontros.addActionListener(e -> new VisualizarEncontrosGUI().setVisible(true));

        btnVerAniversariantes.addActionListener(e -> new AniversariantesGUI().setVisible(true));

        setVisible(true);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton button = new JButton(texto);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_BASE_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // ...
            }
        }

        SwingUtilities.invokeLater(() -> new MenuPrincipal());
    }
}