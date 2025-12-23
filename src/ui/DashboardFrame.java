package ui;

import service.AuthService;
import service.TaskFileManager;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final AuthService authService;
    private final TaskFileManager taskManager;

    private JPanel contentPanel;
    private JButton tasksButton;
    private JButton reportsButton;
    private JButton settingsButton;

    public DashboardFrame(AuthService authService) {
        this.authService = authService;
        this.taskManager = new TaskFileManager();
        initUI();
    }

    /*
     * =====================================================
     * ROOT
     * =====================================================
     */

    private void initUI() {
        setTitle("TaskFlow");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Auto fullscreen
        setSize(1320, 780); // Fallback size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 247, 250));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setBorder(new EmptyBorder(36, 36, 36, 36));

        add(contentPanel, BorderLayout.CENTER);

        showTaskList();
    }

    /*
     * =====================================================
     * HEADER – DARK EDITORIAL (SELARAS)
     * =====================================================
     */

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 72));
        header.setBackground(new Color(15, 23, 42)); // slate-900
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(30, 41, 59)));

        JLabel logo = new JLabel("TaskFlow");
        logo.setFont(new Font("Inter", Font.BOLD, 22));
        logo.setForeground(UIConstants.PRIMARY_COLOR);
        logo.setBorder(new EmptyBorder(0, 32, 0, 0));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        right.setOpaque(false);

        JLabel user = new JLabel(authService.getCurrentUser().getUsername());
        user.setFont(UIConstants.FONT_REGULAR);
        user.setForeground(new Color(226, 232, 240)); // slate-200

        JButton logout = ghostButton("Logout");
        logout.setForeground(new Color(203, 213, 225)); // slate-300
        logout.addActionListener(e -> handleLogout());

        right.add(user);
        right.add(logout);

        header.add(logo, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    /*
     * =====================================================
     * SIDEBAR
     * =====================================================
     */

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(17, 24, 39));
        sidebar.setBorder(new EmptyBorder(28, 12, 28, 12));

        JLabel nav = new JLabel("NAVIGATION");
        nav.setFont(new Font("Inter", Font.BOLD, 10));
        nav.setForeground(new Color(148, 163, 184));
        nav.setBorder(new EmptyBorder(0, 16, 16, 0));

        tasksButton = pillButton("Tasks", true);
        reportsButton = pillButton("Reports", false);
        settingsButton = pillButton("Settings", false);

        tasksButton.addActionListener(e -> {
            activate(tasksButton, reportsButton, settingsButton);
            showTaskList();
        });

        reportsButton.addActionListener(e -> {
            activate(reportsButton, tasksButton, settingsButton);
            showReports();
        });

        settingsButton.addActionListener(e -> {
            activate(settingsButton, tasksButton, reportsButton);
            showSettings();
        });

        sidebar.add(nav);
        sidebar.add(tasksButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(reportsButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(settingsButton);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    /*
     * =====================================================
     * BUTTON – PILL (macOS SAFE)
     * =====================================================
     */

    private JButton pillButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(8, 6, getWidth() - 16, getHeight() - 12, 18, 18);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(
                        getText(),
                        28,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);

                g2.dispose();
            }
        };

        btn.setFont(UIConstants.FONT_REGULAR);
        btn.setForeground(Color.WHITE);
        btn.setBackground(active ? UIConstants.PRIMARY_COLOR : new Color(17, 24, 39));

        btn.setContentAreaFilled(true);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        return btn;
    }

    private void activate(JButton active, JButton... inactive) {
        active.setBackground(UIConstants.PRIMARY_COLOR);
        for (JButton btn : inactive) {
            btn.setBackground(new Color(17, 24, 39));
        }
        repaint();
    }

    /*
     * =====================================================
     * CONTENT
     * =====================================================
     */

    private void showTaskList() {
        contentPanel.removeAll();
        contentPanel.add(
                new TaskListFrame(taskManager, authService).getContentPanel(),
                BorderLayout.CENTER);
        refresh();
    }

    private void showReports() {
        contentPanel.removeAll();
        contentPanel.add(
                new ReportFrame(taskManager).getContentPanel(),
                BorderLayout.CENTER);
        refresh();
    }

    private void showSettings() {
        contentPanel.removeAll();
        contentPanel.add(
                new SettingsPanel(authService),
                BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /*
     * =====================================================
     * GHOST BUTTON
     * =====================================================
     */

    private JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_REGULAR);
        btn.setContentAreaFilled(true);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /*
     * =====================================================
     * LOGOUT
     * =====================================================
     */

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Logout from TaskFlow?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(authService).setVisible(true));
        }
    }
}
