package ui;

import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setTitle("TaskFlow — Sign In");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(buildLeftPanel());
        mainPanel.add(buildRightPanel());

        add(mainPanel);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(99, 102, 241)); 

        JLabel title = new JLabel("TaskFlow");
        title.setFont(new Font("SF Pro Display", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(70, 180, 350, 50);
        panel.add(title);

        JLabel subtitle = new JLabel("Task Management System");
        subtitle.setFont(new Font("SF Pro Display", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setBounds(70, 235, 300, 30);
        panel.add(subtitle);

        JLabel desc = new JLabel(
                "<html>Organize tasks efficiently,<br>track progress, and stay focused.</html>"
        );
        desc.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        desc.setForeground(new Color(255, 255, 255, 180));
        desc.setBounds(70, 280, 320, 50);
        panel.add(desc);

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        title.setForeground(new Color(30, 30, 30));
        title.setBounds(80, 120, 200, 40);
        panel.add(title);

        JLabel subtitle = new JLabel("Continue to TaskFlow");
        subtitle.setFont(new Font("SF Pro Display", Font.PLAIN, 13));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBounds(80, 165, 300, 20);
        panel.add(subtitle);

        panel.add(buildLabel("USERNAME", 80, 230));
        usernameField = buildInputField(80, 255);
        panel.add(usernameField);

        panel.add(buildLabel("PASSWORD", 80, 320));
        passwordField = new JPasswordField();
        passwordField.setBounds(80, 345, 290, 45);
        styleInput(passwordField);
        panel.add(passwordField);

        JButton loginButton = new ModernButton("Sign In");
        loginButton.setBounds(80, 420, 290, 50);
        loginButton.setFont(new Font("SF Pro Display", Font.BOLD, 15));
        loginButton.setBackground(new Color(99, 102, 241));
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());

        panel.add(loginButton);

        JLabel hint = new JLabel("<html>admin / admin123</html>");
        hint.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        hint.setForeground(new Color(150, 150, 150));
        hint.setBounds(80, 480, 200, 40);
        panel.add(hint);

        return panel;
    }

    private JLabel buildLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SF Pro Display", Font.BOLD, 11));
        label.setForeground(new Color(100, 100, 100));
        label.setBounds(x, y, 120, 20);
        return label;
    }

    private JTextField buildInputField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 290, 45);
        styleInput(field);
        return field;
    }

    private void styleInput(JComponent field) {
        field.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        field.setBackground(new Color(248, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        if (authService.login(username, password)) {
            dispose();
            SwingUtilities.invokeLater(() ->
                    new DashboardFrame(authService).setVisible(true)
            );
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Authentication Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (getModel().isPressed()) {
                g2.setColor(new Color(79, 70, 229));
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(129, 140, 248));
            } else {
                g2.setColor(getBackground());
            }

            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 12, 12
            ));
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
