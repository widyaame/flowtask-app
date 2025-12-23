package ui;

import model.Task;
import service.AuthService;
import service.TaskFileManager;
import util.UIConstants;
import util.RoundedButtonUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TaskListFrame {

    private final TaskFileManager taskManager;
    private JPanel contentPanel;
    private JPanel tableContainer; // Container for CardLayout (Table vs Empty State)
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> filterBox;

    private int hoveredRow = -1;

    public TaskListFrame(TaskFileManager taskManager, AuthService authService) {
        this.taskManager = taskManager;
        initUI();
    }

    /*
     * =====================================================
     * ROOT
     * =====================================================
     */

    private void initUI() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        contentPanel.add(buildHeader(), BorderLayout.NORTH);
        contentPanel.add(buildTableCard(), BorderLayout.CENTER);
    }

    /*
     * =====================================================
     * HEADER
     * =====================================================
     */

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.BACKGROUND_COLOR);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("Tasks");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        filterBox = new JComboBox<>(new String[] { "All Tasks", "Pending", "Progress", "Done" });
        filterBox.setFont(UIConstants.FONT_REGULAR);
        filterBox.setPreferredSize(new Dimension(150, 38));
        filterBox.addActionListener(e -> refresh());

        JButton addBtn = createPrimaryButton("+ Add Task", UIConstants.PRIMARY_COLOR);
        addBtn.addActionListener(e -> showAddTaskDialog());

        actions.add(filterBox);
        actions.add(addBtn);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    /*
     * =====================================================
     * TABLE CARD
     * =====================================================
     */

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new EmptyBorder(12, 12, 16, 12),
                new LineBorder(new Color(226, 232, 240), 1, true)));

        String[] cols = { "ID", "Title", "Description", "Status", "Assigned", "Created", "" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };

        table = new JTable(model);
        table.setFont(UIConstants.FONT_REGULAR);
        table.setRowHeight(64); // Larger rows
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(248, 250, 252)); // Very subtle selection
        table.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        table.setGridColor(UIConstants.BORDER_COLOR);

        // Custom Table Header
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Inter", Font.BOLD, 13));
        th.setForeground(new Color(100, 116, 139));
        th.setBackground(new Color(248, 250, 252));
        th.setPreferredSize(new Dimension(0, 48)); // Taller header
        th.setBorder(new MatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Apply Renderers
        table.setDefaultRenderer(Object.class, new PaddedCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionEditor());

        installHoverEffect();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        // Table Container with CardLayout for Empty State
        tableContainer = new JPanel(new CardLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.add(scroll, "TABLE");
        tableContainer.add(new EmptyStatePanel(), "EMPTY");

        card.add(tableContainer, BorderLayout.CENTER);
        refresh();

        return card;
    }

    /*
     * =====================================================
     * HOVER EFFECT
     * =====================================================
     */

    private void installHoverEffect() {
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }
        });
        // Note: DefaultRenderer is now PaddedCellRenderer, which handles hover logic
        // internally if needed,
        // but since we are swapping renderers per column, we need to ensure they all
        // handle background hover.
        // We will inject hover logic into PaddedCellRenderer and StatusRenderer.
    }

    // Helper to get hover color
    private Color getRowColor(int row, boolean isSelected) {
        if (isSelected)
            return new Color(241, 245, 249); // Selected color
        if (row == hoveredRow)
            return new Color(248, 250, 252); // Hover color
        return Color.WHITE;
    }

    /*
     * =====================================================
     * DATA
     * =====================================================
     */

    private void refresh() {
        model.setRowCount(0);

        List<Task> tasks = filterBox.getSelectedItem().equals("All Tasks")
                ? taskManager.getAllTasks()
                : taskManager.getTasksByStatus((String) filterBox.getSelectedItem());

        for (Task t : tasks) {
            model.addRow(new Object[] {
                    t.getId(),
                    t.getTitle(),
                    t.getDescription(),
                    t.getStatus(),
                    t.getAssignedTo(),
                    t.getCreatedDate(),
                    ""
            });
        }

        // Switch view based on count
        CardLayout cl = (CardLayout) tableContainer.getLayout();
        if (tasks.isEmpty()) {
            cl.show(tableContainer, "EMPTY");
        } else {
            cl.show(tableContainer, "TABLE");
        }
    }

    /*
     * =====================================================
     * BUTTON FACTORY
     * =====================================================
     */

    private JButton createPrimaryButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setUI(new RoundedButtonUI(color, 10));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createMiniButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_SMALL);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(72, 32));
        btn.setUI(new RoundedButtonUI(color, 8));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /*
     * =====================================================
     * STATUS BADGE
     * =====================================================
     */

    class StatusRenderer extends JPanel implements TableCellRenderer {

        public StatusRenderer() {
            super(new FlowLayout(FlowLayout.CENTER, 0, 0));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            removeAll();

            // Handle selection colors
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            if (value == null)
                return this;

            JLabel badge = new JLabel(value.toString());
            badge.setFont(UIConstants.FONT_SMALL);
            badge.setBorder(new EmptyBorder(4, 10, 4, 10));
            badge.setOpaque(true);

            switch (value.toString()) {
                case "Pending" -> {
                    badge.setBackground(new Color(254, 243, 199));
                    badge.setForeground(new Color(180, 83, 9));
                }
                case "Progress" -> {
                    badge.setBackground(new Color(219, 234, 254));
                    badge.setForeground(new Color(30, 64, 175));
                }
                case "Done" -> {
                    badge.setBackground(new Color(220, 252, 231));
                    badge.setForeground(new Color(21, 128, 61));
                }
                default -> {
                    badge.setBackground(new Color(241, 245, 249));
                    badge.setForeground(new Color(100, 116, 139));
                }
            }

            add(badge);
            return this;
        }
    }

    /*
     * =====================================================
     * HOVER ROW RENDERER
     * =====================================================
     */

    /*
     * =====================================================
     * PADDED TEXT CELL RENDERER
     * =====================================================
     */

    class PaddedCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(UIConstants.FONT_REGULAR);
            setBorder(new EmptyBorder(0, 10, 0, 10)); // Horizontal Padding
            setForeground(UIConstants.TEXT_PRIMARY);
            setBackground(getRowColor(row, isSelected));
            return this;
        }
    }

    /*
     * =====================================================
     * EMPTY STATE PANEL
     * =====================================================
     */

    class EmptyStatePanel extends JPanel {
        public EmptyStatePanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(48, 0, 48, 0));

            JLabel icon = new JLabel("📝"); // Simple icon placeholder
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = new JLabel("No tasks found");
            title.setFont(new Font("Inter", Font.BOLD, 16));
            title.setForeground(UIConstants.TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = new JLabel("Get started by creating a new task.");
            sub.setFont(UIConstants.FONT_REGULAR);
            sub.setForeground(UIConstants.TEXT_SECONDARY);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(icon);
            add(Box.createRigidArea(new Dimension(0, 16)));
            add(title);
            add(Box.createRigidArea(new Dimension(0, 8)));
            add(sub);
        }
    }

    /*
     * =====================================================
     * ACTION BUTTONS (HOVER ONLY)
     * =====================================================
     */

    class ActionRenderer extends JPanel implements TableCellRenderer {
        public ActionRenderer() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            removeAll();
            setBackground(getRowColor(row, isSelected));

            if (row == hoveredRow) {
                JButton edit = createMiniButton("Edit", new Color(59, 130, 246));
                JButton del = createMiniButton("Delete", UIConstants.DANGER_COLOR);
                // del.setPreferredSize(new Dimension(32, 32)); // Removed fixed size for text

                add(edit);
                add(del);
            }
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        private int row;

        public ActionEditor() {
            super(new JCheckBox());
            panel.setOpaque(false);

            JButton edit = createMiniButton("Edit", new Color(59, 130, 246));
            JButton del = createMiniButton("Delete", UIConstants.DANGER_COLOR);
            // del.setPreferredSize(new Dimension(32, 32)); // Removed fixed size for text

            edit.addActionListener(e -> {
                fireEditingStopped();
                showEditTaskDialog((int) table.getValueAt(row, 0));
            });

            del.addActionListener(e -> {
                fireEditingStopped();
                deleteTask((int) table.getValueAt(row, 0));
            });

            panel.add(edit);
            panel.add(del);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }
    }

    /*
     * =====================================================
     * DIALOGS
     * =====================================================
     */

    private void showAddTaskDialog() {
        new TaskFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(contentPanel),
                taskManager, null).setVisible(true);
        refresh();
    }

    private void showEditTaskDialog(int id) {
        Task t = taskManager.getTaskById(id);
        if (t != null) {
            new TaskFormDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(contentPanel),
                    taskManager, t).setVisible(true);
            refresh();
        }
    }

    private void deleteTask(int id) {
        if (JOptionPane.showConfirmDialog(
                contentPanel,
                "Delete this task?",
                "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            taskManager.deleteTask(id);
            refresh();
        }
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
