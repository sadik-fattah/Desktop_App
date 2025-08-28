package org.geurcifzone.TestingSide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class CustomTitleBarWithImages extends JFrame {

    public CustomTitleBarWithImages() {
        // Set up the JFrame
        setTitle("Custom Title Bar with Images");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Remove default window decorations
        setUndecorated(true);

        // Create a custom title bar
        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(30, 30, 30));
        titleBar.setPreferredSize(new Dimension(getWidth(), 40));
        titleBar.setLayout(new BorderLayout());

        // Add title text
        JLabel titleLabel = new JLabel("  Custom Title Bar");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleBar.add(titleLabel, BorderLayout.WEST);

        // Add window controls (minimize, maximize, close)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);

        // Create buttons with images
        JButton minButton = createImageButton("src/main/resources/IMG/mini.png", "Minimize");
        JButton maxButton = createImageButton("src/main/resources/IMG/maxi.png", "Maximize");
        JButton closeButton = createImageButton("src/main/resources/IMG/close.png", "Close");

        // If images are not found, use fallback text buttons
        if (minButton == null) minButton = createTitleButton("—");
        if (maxButton == null) maxButton = createTitleButton("□");
        if (closeButton == null) closeButton = createTitleButton("×");

        // Add action listeners to buttons
        minButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        maxButton.addActionListener(e -> toggleMaximize());
        closeButton.addActionListener(e -> System.exit(0));

        controlPanel.add(minButton);
        controlPanel.add(maxButton);
        controlPanel.add(closeButton);

        titleBar.add(controlPanel, BorderLayout.EAST);

        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(50, 50, 50));
        contentPanel.setLayout(new BorderLayout());

        // Add components to content panel
        JLabel welcomeLabel = new JLabel("Custom Title Bar with Image Buttons", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add instructions
        JTextArea instructions = new JTextArea();
        instructions.setText("How to use image buttons:\n\n" +
                "1. Create a 'resources' folder in your project\n" +
                "2. Add your image files (minimize.png, maximize.png, close.png)\n" +
                "3. Load them using getClass().getResource()\n" +
                "4. Scale them appropriately for button icons\n\n" +
                "If images aren't found, the fallback text buttons will be used.");
        instructions.setForeground(Color.LIGHT_GRAY);
        instructions.setBackground(new Color(70, 70, 70));
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setEditable(false);
        instructions.setMargin(new Insets(10, 10, 10, 10));
        contentPanel.add(instructions, BorderLayout.SOUTH);

        // Add components to frame
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Add mouse listener for dragging the window
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragMousePressed(e);
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                dragMouseDragged(e);
            }
        });

        // Center the window
        setLocationRelativeTo(null);
    }

    private JButton createImageButton(String imageName, String tooltip) {
        try {
            // Try to load the image from resources
            URL imageUrl = getClass().getResource("/resources/" + imageName);
            if (imageUrl == null) {
                System.out.println("Image not found: " + imageName);
                return null;
            }

            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage()
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);

            JButton button = new JButton(icon);
            button.setToolTipText(tooltip);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setPreferredSize(new Dimension(30, 30));

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (tooltip.equals("Close")) {
                        button.setBackground(new Color(232, 17, 35));
                    } else {
                        button.setBackground(new Color(80, 80, 80));
                    }
                    button.setOpaque(true);
                }

                public void mouseExited(MouseEvent e) {
                    button.setBackground(null);
                    button.setOpaque(false);
                }
            });

            return button;
        } catch (Exception e) {
            System.out.println("Error loading image: " + imageName);
            return null;
        }
    }

    private JButton createTitleButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(30, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button.getText().equals("×")) {
                    button.setBackground(new Color(232, 17, 35));
                } else {
                    button.setBackground(new Color(80, 80, 80));
                }
                button.setOpaque(true);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(null);
                button.setOpaque(false);
            }
        });

        return button;
    }

    private void toggleMaximize() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    // Variables for window dragging
    private Point initialClick;

    private void dragMousePressed(MouseEvent e) {
        initialClick = e.getPoint();
    }

    private void dragMouseDragged(MouseEvent e) {
        // Get location of Window
        int thisX = getLocation().x;
        int thisY = getLocation().y;

        // Determine how much the mouse moved since the initial click
        int xMoved = e.getX() - initialClick.x;
        int yMoved = e.getY() - initialClick.y;

        // Move window to this position
        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        setLocation(X, Y);
    }

    public static void main(String[] args) {
        // Set the look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            CustomTitleBarWithImages frame = new CustomTitleBarWithImages();
            frame.setVisible(true);
        });
    }
}