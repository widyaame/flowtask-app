package ui;

import service.AuthService;
import util.RoundedButtonUI;
import util.UIConstants;
import model.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final AuthService authService;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public SettingsPanel(AuthService authService) {
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(32, 48, 32, 48));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 8));
        header.setBackground(UIConstants.BACKGROUND_COLOR);
        header.setBorder(new EmptyBorder(0, 0, 32, 0));

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage your account and preferences");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIConstants.BORDER_COLOR),
                new EmptyBorder(32, 32, 32, 32)));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.setMaximumSize(new Dimension(600, 500)); // Limit width

        JLabel sectionTitle = new JLabel("Security");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 18));
        sectionTitle.setForeground(UIConstants.TEXT_PRIMARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(sectionTitle);
        form.add(Box.createRigidArea(new Dimension(0, 24)));

        form.add(inputBlock("Current Password", currentPasswordField = new JPasswordField()));
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        form.add(inputBlock("New Password", newPasswordField = new JPasswordField()));
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        form.add(inputBlock("Confirm New Password", confirmPasswordField = new JPasswordField()));
        form.add(Box.createRigidArea(new Dimension(0, 32)));

        JButton saveBtn = new JButton("Update Password");
        saveBtn.setFont(UIConstants.FONT_BUTTON);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setUI(new RoundedButtonUI(UIConstants.PRIMARY_COLOR, 8));
        saveBtn.setPreferredSize(new Dimension(180, 42));
        saveBtn.setMaximumSize(new Dimension(180, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleChangePassword());

        form.add(saveBtn);
        form.add(Box.createVerticalGlue()); // Push content up

        // Wrap in a container to handle alignment in BorderLayout
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(UIConstants.BACKGROUND_COLOR);
        wrapper.add(form);

        return wrapper;
    }

    private JPanel inputBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBackground(Color.WHITE);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_SMALL); // Fixed typo from FONT_Small
        if (label.getFont() == null)
            label.setFont(new Font("Inter", Font.PLAIN, 13)); // Fallback
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(UIConstants.FONT_REGULAR);
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIConstants.BORDER_COLOR),
                new EmptyBorder(8, 12, 8, 12)));
        ((JComponent) field).setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(label);
        block.add(Box.createRigidArea(new Dimension(0, 8)));
        block.add(field);

        return block;
    }

    private void handleChangePassword() {
        String current = new String(currentPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        User user = authService.getCurrentUser();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!user.getPassword().equals(current)) {
            JOptionPane.showMessageDialog(this, "Incorrect current password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.updatePassword(user.getUsername(), newPass)) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
}
