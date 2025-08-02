package org.guercifzone;


import org.guercifzone.DeepSeek.Gui.MainFrame;

public class App {
    public static void main(String[] args) {
        // Create and show the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

