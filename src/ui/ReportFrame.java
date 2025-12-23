package ui;

import model.Task;
import service.TaskFileManager;
import util.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ReportFrame {

    private final TaskFileManager taskManager;
    private JPanel contentPanel;

    public ReportFrame(TaskFileManager taskManager) {
        this.taskManager = taskManager;
        initializeUI();
    }
    
    private void initializeUI() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        contentPanel.add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildReportContent());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);

        contentPanel.add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Insights & productivity overview");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.add(title);
        text.add(Box.createRigidArea(new Dimension(0, 6)));
        text.add(subtitle);

        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JPanel buildReportContent() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);

        wrapper.add(buildStatusSummary());
        wrapper.add(Box.createRigidArea(new Dimension(0, 28)));
        wrapper.add(buildCompletionCard());
        wrapper.add(Box.createRigidArea(new Dimension(0, 28)));
        wrapper.add(buildUserDistribution());
        wrapper.add(Box.createRigidArea(new Dimension(0, 28)));
        wrapper.add(buildMiniStats());

        return wrapper;
    }

    private JPanel buildStatusSummary() {
        JPanel grid = new JPanel(new GridLayout(1, 3, 20, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        grid.add(createStatCard("Pending", taskManager.countByStatus("Pending"), new Color(251, 146, 60)));
        grid.add(createStatCard("In Progress", taskManager.countByStatus("Progress"), new Color(59, 130, 246)));
        grid.add(createStatCard("Completed", taskManager.countByStatus("Done"), new Color(34, 197, 94)));

        return grid;
    }

    private JPanel createStatCard(String title, int value, Color accent) {
        JPanel card = createBaseCard();
        card.setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Inter", Font.BOLD, 36));
        valueLabel.setForeground(accent);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_REGULAR);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        left.add(valueLabel);
        left.add(Box.createRigidArea(new Dimension(0, 6)));
        left.add(titleLabel);

        card.add(left, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildCompletionCard() {
        JPanel card = createBaseCard();
        card.setLayout(new BorderLayout());

        JLabel title = new JLabel("Overall Completion");
        title.setFont(UIConstants.FONT_HEADER);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        List<Task> all = taskManager.getAllTasks();
        int total = all.size();
        int done = taskManager.countByStatus("Done");
        int percent = total > 0 ? (done * 100 / total) : 0;

        JLabel percentLabel = new JLabel(percent + "%");
        percentLabel.setFont(new Font("Inter", Font.BOLD, 28));
        percentLabel.setForeground(UIConstants.PRIMARY_COLOR);

        JLabel desc = new JLabel(done + " of " + total + " tasks completed");
        desc.setFont(UIConstants.FONT_REGULAR);
        desc.setForeground(UIConstants.TEXT_SECONDARY);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(percentLabel, BorderLayout.EAST);

        JPanel bar = new ProgressBar(percent);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 0, 0, 0));
        content.add(desc);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(bar);

        card.add(top, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildUserDistribution() {
        JPanel card = createBaseCard();
        card.setLayout(new BorderLayout());

        JLabel title = new JLabel("Tasks by Team Member");
        title.setFont(UIConstants.FONT_HEADER);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setBorder(new EmptyBorder(16, 0, 0, 0));

        Map<String, Integer> map = new HashMap<>();
        for (Task t : taskManager.getAllTasks()) {
            map.put(t.getAssignedTo(), map.getOrDefault(t.getAssignedTo(), 0) + 1);
        }

        int max = map.values().stream().max(Integer::compare).orElse(1);

        for (var e : map.entrySet()) {
            list.add(createUserRow(e.getKey(), e.getValue(), max));
            list.add(Box.createRigidArea(new Dimension(0, 16)));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel createUserRow(String user, int count, int max) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);

        JLabel name = new JLabel("👤 " + user);
        name.setFont(UIConstants.FONT_REGULAR);

        JLabel val = new JLabel(count + " tasks");
        val.setFont(UIConstants.FONT_SMALL);
        val.setForeground(UIConstants.TEXT_SECONDARY);

        JPanel text = new JPanel(new BorderLayout());
        text.setOpaque(false);
        text.add(name, BorderLayout.NORTH);
        text.add(val, BorderLayout.SOUTH);

        JPanel bar = new ProgressBar((int) (count * 100.0 / max));
        bar.setPreferredSize(new Dimension(0, 10));

        row.add(text, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildMiniStats() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        List<Task> all = taskManager.getAllTasks();
        int total = all.size();
        int active = total - taskManager.countByStatus("Done");
        int rate = total > 0 ? (taskManager.countByStatus("Done") * 100 / total) : 0;
        int members = (int) all.stream().map(Task::getAssignedTo).distinct().count();

        grid.add(createMiniCard("Total Tasks", total));
        grid.add(createMiniCard("Active Tasks", active));
        grid.add(createMiniCard("Completion Rate", rate + "%"));
        grid.add(createMiniCard("Team Members", members));

        return grid;
    }

    private JPanel createMiniCard(String title, Object value) {
        JPanel card = createBaseCard();
        card.setLayout(new BorderLayout());

        JLabel t = new JLabel(title);
        t.setFont(UIConstants.FONT_SMALL);
        t.setForeground(UIConstants.TEXT_SECONDARY);

        JLabel v = new JLabel(String.valueOf(value));
        v.setFont(new Font("Inter", Font.BOLD, 24));
        v.setForeground(UIConstants.TEXT_PRIMARY);

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }

    /* =====================================================
       SHARED COMPONENTS
       ===================================================== */

    private JPanel createBaseCard() {
        JPanel card = new JPanel();
        card.setBackground(UIConstants.SURFACE_COLOR);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        installHover(card);
        return card;
    }

    private void installHover(JPanel card) {
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(new Color(203, 213, 225), 1, true),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(new Color(226, 232, 240), 1, true),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }
        });
    }

    /* =====================================================
       PROGRESS BAR
       ===================================================== */

    static class ProgressBar extends JPanel {
        private final int percent;

        ProgressBar(int percent) {
            this.percent = percent;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(241, 245, 249));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            int w = (int) (getWidth() * percent / 100.0);
            g2.setColor(UIConstants.PRIMARY_COLOR);
            g2.fillRoundRect(0, 0, w, getHeight(), 10, 10);
        }
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
