package ui;

import model.Task;
import service.TaskFileManager;
import util.UIConstants;
import util.RoundedButtonUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskFormDialog extends JDialog {

    private final TaskFileManager taskManager;
    private final Task existingTask;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusBox;
    private JTextField assignedToField;

    public TaskFormDialog(JFrame parent, TaskFileManager taskManager, Task existingTask) {
        super(parent, true);
        this.taskManager = taskManager;
        this.existingTask = existingTask;
        initUI();
    }

    /*
     * =====================================================
     * ROOT – MODERN SAAS STYLE
     * =====================================================
     */

    private void initUI() {
        setTitle(existingTask == null ? "New Task" : "Edit Task");
        setSize(500, 620); // Slightly narrower, taller
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        if (existingTask != null)
            populate();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.BACKGROUND_COLOR);
        header.setBorder(new EmptyBorder(32, 40, 0, 40));

        JLabel title = new JLabel(existingTask == null ? "Create New Task" : "Edit Task");
        title.setFont(new Font("Inter", Font.BOLD, 24));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        header.add(title, BorderLayout.CENTER);
        return header;
    }

    /*
     * =====================================================
     * CONTENT
     * =====================================================
     */

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UIConstants.BACKGROUND_COLOR);
        content.setBorder(new EmptyBorder(24, 40, 24, 40));

        content.add(inputBlock("Title", titleField = styledTextField()));
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        content.add(inputBlock("Description", styledTextArea()));
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        content.add(inputBlock("Status", statusBox = styledCombo()));
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        content.add(inputBlock("Assigned to", assignedToField = styledTextField()));

        return content;
    }

    /*
     * =====================================================
     * INPUT BLOCK
     * =====================================================
     */

    private JPanel inputBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        ((JComponent) field).setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(label);
        block.add(Box.createRigidArea(new Dimension(0, 8)));
        block.add(field);

        return block;
    }

    /*
     * =====================================================
     * FIELDS – ROUNDED STYLE
     * =====================================================
     */

    private JTextField styledTextField() {
        JTextField field = new JTextField();
        field.setFont(UIConstants.FONT_REGULAR);
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIConstants.BORDER_COLOR),
                new EmptyBorder(8, 12, 8, 12)));
        return field;
    }

    private JScrollPane styledTextArea() {
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(UIConstants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scroll.setBorder(new MatteBorder(1, 1, 1, 1, UIConstants.BORDER_COLOR));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        return scroll;
    }

    private JComboBox<String> styledCombo() {
        JComboBox<String> combo = new JComboBox<>(new String[] { "Pending", "Progress", "Done" });
        combo.setFont(UIConstants.FONT_REGULAR);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        combo.setBackground(Color.WHITE);
        // Note: JComboBox border styling is complex in Swing, keeping simple for now or
        // using UIConstants
        return combo;
    }

    /*
     * =====================================================
     * FOOTER – ACTIONS
     * =====================================================
     */

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setBackground(UIConstants.BACKGROUND_COLOR);
        footer.setBorder(new EmptyBorder(0, 40, 32, 40));

        JButton cancel = new JButton("Cancel");
        cancel.setFont(UIConstants.FONT_BUTTON);
        cancel.setBackground(Color.WHITE);
        cancel.setForeground(UIConstants.TEXT_PRIMARY);
        cancel.setFocusPainted(false);
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIConstants.BORDER_COLOR),
                new EmptyBorder(8, 16, 8, 16)));
        cancel.setContentAreaFilled(true);
        cancel.addActionListener(e -> dispose());

        JButton save = new JButton(existingTask == null ? "Create Task" : "Save Changes");
        save.setFont(UIConstants.FONT_BUTTON);
        save.setBackground(UIConstants.PRIMARY_COLOR);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        save.setOpaque(true);
        save.setBorderPainted(false);
        save.setUI(new RoundedButtonUI(UIConstants.PRIMARY_COLOR, 8));
        save.setPreferredSize(new Dimension(140, 40));
        save.addActionListener(e -> handleSave());

        footer.add(cancel);
        footer.add(save);

        return footer;
    }

    /*
     * =====================================================
     * DATA
     * =====================================================
     */

    private void populate() {
        titleField.setText(existingTask.getTitle());
        descriptionArea.setText(existingTask.getDescription());
        statusBox.setSelectedItem(existingTask.getStatus());
        assignedToField.setText(existingTask.getAssignedTo());
    }

    private void handleSave() {
        String title = titleField.getText().trim();
        String assigned = assignedToField.getText().trim();

        if (title.isEmpty() || assigned.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Title and Assigned To are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (existingTask == null) {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                taskManager.addTask(new Task(
                        0,
                        title,
                        descriptionArea.getText(),
                        (String) statusBox.getSelectedItem(),
                        assigned,
                        date));
            } else {
                existingTask.setTitle(title);
                existingTask.setDescription(descriptionArea.getText());
                existingTask.setStatus((String) statusBox.getSelectedItem());
                existingTask.setAssignedTo(assigned);
                taskManager.updateTask(existingTask);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
