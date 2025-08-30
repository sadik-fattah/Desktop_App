package org.geurcifzone.GetBloggerContent;

import org.geurcifzone.Classes.MyButton;
import org.geurcifzone.Classes.MyTextView;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BG_Gui extends JFrame {
    private JPanel contentPanel;
    private JLabel titleLbl;
    private static MyTextView bloggerLinkTextField;
    private MyButton btn;
    private Point initialClick;

    public BG_Gui(){
        // MainFrame
        setTitle("Blogger Content Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200); // Reduced height since we removed instructions
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(new Color(45, 45, 45));
        setResizable(false);
        // Create a custom title bar
        JPanel titleBar = createTitleBar();

        // Main content panel
        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(45, 45, 45));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(new Color(45, 45, 45));

        titleLbl = new JLabel("Blogger Post URL:");
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));

        bloggerLinkTextField = new MyTextView();
        bloggerLinkTextField.setColumns(40);
        bloggerLinkTextField.setLabelText("Paste blog post URL here");
        bloggerLinkTextField.setForeground(Color.WHITE);
        bloggerLinkTextField.setCaretColor(Color.ORANGE);

        btn = new MyButton();
        btn.setText("Get Content");
        btn.setColor1(new Color(255, 104, 0));
        btn.setColor2(new Color(230, 250, 34));
        btn.setSizeSpeed(0.5F);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 35));

        inputPanel.add(titleLbl, BorderLayout.WEST);
        inputPanel.add(bloggerLinkTextField, BorderLayout.CENTER);
        inputPanel.add(btn, BorderLayout.EAST);

        // Status label
        JLabel statusLabel = new JLabel("Enter a Blogger post URL to extract content as XML");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        contentPanel.add(inputPanel, BorderLayout.CENTER);
        contentPanel.add(statusLabel, BorderLayout.SOUTH);

        add(titleBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Right-click context menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.setFont(new Font("Arial", Font.PLAIN, 12));
        popupMenu.add(pasteItem);

        bloggerLinkTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        pasteItem.addActionListener(e -> bloggerLinkTextField.paste());

        btn.addActionListener(e -> RunInBack());

        pack();
        setVisible(true);
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(30, 30, 30));
        titleBar.setPreferredSize(new Dimension(500, 40));
        titleBar.setLayout(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        JLabel titleLabel = new JLabel("Blogger Content Extractor");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleBar.add(titleLabel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);

        JButton minButton = createImageButton("Minimize.png", "Minimize");
        JButton maxButton = createImageButton("Maximize.png", "Maximize");
        JButton closeButton = createImageButton("Close.png", "Close");

        if (minButton == null) minButton = createTitleButton("—");
        if (maxButton == null) maxButton = createTitleButton("□");
        if (closeButton == null) closeButton = createTitleButton("×");

        minButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        maxButton.addActionListener(e -> toggleMaximize());
        closeButton.addActionListener(e -> System.exit(0));

        controlPanel.add(minButton);
        controlPanel.add(maxButton);
        controlPanel.add(closeButton);

        titleBar.add(controlPanel, BorderLayout.EAST);

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

        return titleBar;
    }

    private JButton createImageButton(String imageName, String tooltip) {
        try {
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

    private void dragMousePressed(MouseEvent e) {
        initialClick = e.getPoint();
    }

    private void dragMouseDragged(MouseEvent e) {
        int thisX = getLocation().x;
        int thisY = getLocation().y;

        int xMoved = e.getX() - initialClick.x;
        int yMoved = e.getY() - initialClick.y;

        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        setLocation(X, Y);
    }

    public static void RunInBack() {
        String blogUrl = bloggerLinkTextField.getText().toString().trim();

        if (blogUrl.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Please enter a Blogger post URL.\nThe application will generate an XML file from the content.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add proper URL validation
        if (!blogUrl.startsWith("http")) {
            blogUrl = "https://" + blogUrl;
        }

        String feedUrl = blogUrl + "/feeds/posts/default?alt=rss";
        String outputFile = "blogger_content.xml";

        try {
            URL url = new URL(feedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new File(outputFile));

                transformer.transform(source, result);

                JOptionPane.showMessageDialog(null,
                        "Blog content successfully saved to:\n" + new File(outputFile).getAbsolutePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Failed to fetch blog content.\nHTTP Response Code: " + responseCode +
                                "\nPlease check the URL and try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage() +
                            "\nPlease check the URL and try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            BG_Gui bgGui = new BG_Gui();
            bgGui.setVisible(true);
        });
    }
}