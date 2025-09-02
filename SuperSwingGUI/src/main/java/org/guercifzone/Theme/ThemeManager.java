package org.guercifzone.Theme;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private static ThemeManager instance;
    private Map<String, Theme> themes = new HashMap<>();
    private Theme currentTheme;

    private ThemeManager() {
        initializeThemes();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private void initializeThemes() {
        // Dark theme
        Theme darkTheme = new Theme();
        darkTheme.setName("Dark");
        darkTheme.setBackgroundColor(new Color(45, 45, 45));
        darkTheme.setForegroundColor(Color.WHITE);
        darkTheme.setButtonColor(new Color(70, 70, 70));
        darkTheme.setAccentColor(new Color(0, 150, 136));
        themes.put("dark", darkTheme);

        // Light theme
        Theme lightTheme = new Theme();
        lightTheme.setName("Light");
        lightTheme.setBackgroundColor(Color.WHITE);
        lightTheme.setForegroundColor(Color.BLACK);
        lightTheme.setButtonColor(new Color(240, 240, 240));
        lightTheme.setAccentColor(new Color(33, 150, 243));
        themes.put("light", lightTheme);

        currentTheme = lightTheme;
    }

    public void applyTheme(Component component) {
        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent) component;
            jComponent.setBackground(currentTheme.getBackgroundColor());
            jComponent.setForeground(currentTheme.getForegroundColor());

            if (component instanceof JButton) {
                component.setBackground(currentTheme.getButtonColor());
            }
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyTheme(child);
            }
        }
    }

    public void setTheme(String themeName) {
        Theme theme = themes.get(themeName);
        if (theme != null) {
            currentTheme = theme;
        }
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }
}

// Theme class
class Theme {
    private String name;
    private Color backgroundColor;
    private Color foregroundColor;
    private Color buttonColor;
    private Color accentColor;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }

    public Color getForegroundColor() { return foregroundColor; }
    public void setForegroundColor(Color foregroundColor) { this.foregroundColor = foregroundColor; }

    public Color getButtonColor() { return buttonColor; }
    public void setButtonColor(Color buttonColor) { this.buttonColor = buttonColor; }

    public Color getAccentColor() { return accentColor; }
    public void setAccentColor(Color accentColor) { this.accentColor = accentColor; }
}