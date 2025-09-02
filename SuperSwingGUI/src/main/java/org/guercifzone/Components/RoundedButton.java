package org.guercifzone.Components;



import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private int cornerRadius = 20;
    private Color hoverBackgroundColor = new Color(200, 200, 200);
    private Color pressedBackgroundColor = new Color(150, 150, 150);

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    public RoundedButton(String text, int cornerRadius) {
        this(text);
        this.cornerRadius = cornerRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(getBackground());
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border painting
    }

    // Getters and setters
    public int getCornerRadius() { return cornerRadius; }
    public void setCornerRadius(int cornerRadius) { this.cornerRadius = cornerRadius; }

    public Color getHoverBackgroundColor() { return hoverBackgroundColor; }
    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() { return pressedBackgroundColor; }
    public void setPressedBackgroundColor(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
}