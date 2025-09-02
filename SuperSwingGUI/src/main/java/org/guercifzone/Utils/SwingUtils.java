package org.guercifzone.Utils;



import javax.swing.*;
import java.awt.*;

public class SwingUtils {

    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }

    public static void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Handle exception
        }
    }

    public static ImageIcon createIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
}