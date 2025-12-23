package util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class RoundedButtonUI extends BasicButtonUI {

    private final Color baseColor;
    private final int radius;

    public RoundedButtonUI(Color baseColor, int radius) {
        this.baseColor = baseColor;
        this.radius = radius;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = baseColor;
        if (b.getModel().isPressed()) {
            bg = baseColor.darker();
        } else if (b.getModel().isRollover()) {
            bg = baseColor.brighter();
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radius, radius);

        super.paint(g2, c);
        g2.dispose();
    }
}
