package org.guercifzone;


import org.guercifzone.Simple.Gui_.Adminframe;

public class App {
    public static void main(String[] args) {
        // Create and show the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            Adminframe frame = new Adminframe();
            frame.setVisible(true);
        });
    }
}

