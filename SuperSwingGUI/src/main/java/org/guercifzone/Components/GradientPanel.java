package org.guercifzone.Components;



import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color startColor = Color.BLUE;
    private Color endColor = Color.CYAN;
    private boolean horizontalGradient = true;

    public GradientPanel() {
        super();
    }

    public GradientPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradientPaint;
        if (horizontalGradient) {
            gradientPaint = new GradientPaint(
                    0, 0, startColor,
                    getWidth(), 0, endColor
            );
        } else {
            gradientPaint = new GradientPaint(
                    0, 0, startColor,
                    0, getHeight(), endColor
            );
        }

        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    // Getters and setters
    public Color getStartColor() { return startColor; }
    public void setStartColor(Color startColor) { this.startColor = startColor; }

    public Color getEndColor() { return endColor; }
    public void setEndColor(Color endColor) { this.endColor = endColor; }

    public boolean isHorizontalGradient() { return horizontalGradient; }
    public void setHorizontalGradient(boolean horizontalGradient) {
        this.horizontalGradient = horizontalGradient;
    }
}