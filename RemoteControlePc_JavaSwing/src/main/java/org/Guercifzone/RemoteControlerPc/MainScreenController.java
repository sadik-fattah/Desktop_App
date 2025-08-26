package org.Guercifzone.RemoteControlerPc;

import org.Guercifzone.Classes.AvatarFile;
import org.Guercifzone.IPAdress.GetFreePort;
import org.Guercifzone.IPAdress.GetMyIpAddress;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author varun
 */
public class MainScreenController extends JFrame implements ActionListener {

    public static MainScreenController mainScreenController;
    public static ServerSocket serverSocket = null;
    public static Socket clientSocket = null;
    public static InputStream inputStream = null;
    public static OutputStream outputStream = null;
    public static ObjectOutputStream objectOutputStream = null;
    public static ObjectInputStream objectInputStream = null;
    private Stack<String> pathStack;

    private JPanel tilePane;
    private JPanel borderPane;
    private JLabel ipAddressLabel;
    private JLabel portNumberLabel;
    private JLabel connectionStatusLabel;
    private JButton resetButton;
    private JLabel messageLabel;
    private JButton backButton;
    private JButton rootButton;
    private JLabel pathLabel;
    private JScrollPane scrollPane;
    private JPanel mainPanel;

    public MainScreenController() {
        pathStack = new Stack<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Remote Control PC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        mainPanel = new JPanel(new BorderLayout());

        // Top panel for connection info
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        connectionPanel.add(new JLabel("IP Address:"));
        ipAddressLabel = new JLabel();
        connectionPanel.add(ipAddressLabel);

        connectionPanel.add(new JLabel("Port:"));
        portNumberLabel = new JLabel();
        connectionPanel.add(portNumberLabel);

        connectionPanel.add(new JLabel("Status:"));
        connectionStatusLabel = new JLabel("Not Connected");
        connectionPanel.add(connectionStatusLabel);

        resetButton = new JButton("Reset Connection");
        resetButton.addActionListener(this);
        connectionPanel.add(resetButton);

        topPanel.add(connectionPanel);

        // Message label
        messageLabel = new JLabel();
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(messageLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for file display
        tilePane = new JPanel(new GridLayout(0, 4, 10, 10));
        scrollPane = new JScrollPane(tilePane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for navigation
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        backButton = new JButton("Back");
        backButton.addActionListener(this);
        bottomPanel.add(backButton);

        rootButton = new JButton("Root");
        rootButton.addActionListener(this);
        bottomPanel.add(rootButton);

        pathLabel = new JLabel();
        bottomPanel.add(pathLabel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    public MainScreenController getMainScreenController() {
        return MainScreenController.mainScreenController;
    }

    public void setMainScreenController(MainScreenController mainScreenController) {
        MainScreenController.mainScreenController = mainScreenController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == resetButton) {
            resetConnection(event);
        } else if (event.getSource() == backButton) {
            previousLocation();
        } else if (event.getSource() == rootButton) {
            fetchRootDirectory();
        }
    }

    private void resetConnection(ActionEvent event) {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        ClientToAndroid.closeConnectionToAndroid();
        setConnectionDetails();
    }

    private void fetchRootDirectory() {
        ClientToAndroid.fetchDirectory("/");
    }

    private void previousLocation() {
        if (pathStack.isEmpty()) {
            backButton.setEnabled(false);
            return;
        }

        String currentPath = pathStack.peek();
        if (currentPath == null || currentPath.equals("/")) {
            backButton.setEnabled(false);
            return;
        }
        pathStack.pop();
        currentPath = pathStack.isEmpty() ? "/" : pathStack.peek();
        ClientToAndroid.fetchDirectory(currentPath);
    }

    public void initialize() {
        setConnectionDetails();
        setVisible(true);
    }

    private void setConnectionDetails() {
        String ipAddresses[] = new GetMyIpAddress().ipAddress();
        String connectionStatus = "Not Connected";
        int port = new GetFreePort().getFreePort();
        String ipAddress = ipAddresses[0];
        if (ipAddresses[1] != null) {
            ipAddress = ipAddress + " | " + ipAddresses[1];
        }
        ipAddressLabel.setText(ipAddress);
        portNumberLabel.setText(Integer.toString(port));
        connectionStatusLabel.setText(connectionStatus);
        if (ipAddresses[0].equals("127.0.0.1")) {
            showMessage("Connect your PC to Android phone hotspot or" +
                    " connect both devices to a local network.");
        } else {
            try {
                serverSocket = new ServerSocket(port);
                startServer(port);
            } catch(Exception e) {
                showMessage("Error in initializing server");
                e.printStackTrace();
            }
        }
    }

    private void startServer(int port) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                new Server().connect(resetButton, connectionStatusLabel,
                        messageLabel, port);
                return null;
            }
        };
        worker.execute();
    }

    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setText(message);
        });
    }

    public void displayPath(String path) {
        pathStack.push(path);
        SwingUtilities.invokeLater(() -> {
            pathLabel.setText(path);
            pathLabel.setEnabled(true);
        });
    }

    public void showImage(String name, String path) {
        showMessage(name);
        SwingUtilities.invokeLater(() -> {
            tilePane.removeAll();
            tilePane.setLayout(new BorderLayout());

            ImageIcon imageIcon = new ImageIcon(path);
            JLabel imageLabel = new JLabel(imageIcon);
            tilePane.add(imageLabel, BorderLayout.CENTER);

            tilePane.revalidate();
            tilePane.repaint();
        });
    }

    public void closeImageViewer() {
        SwingUtilities.invokeLater(() -> {
            tilePane.removeAll();
            tilePane.setLayout(new GridLayout(0, 4, 10, 10));
            tilePane.revalidate();
            tilePane.repaint();
        });
    }

    public void showFiles(ArrayList<AvatarFile> filesInFolder) {
        SwingUtilities.invokeLater(() -> {
            tilePane.removeAll();
            tilePane.setLayout(new GridLayout(0, 4, 10, 10));

            for (AvatarFile file : filesInFolder) {
                JPanel filePanel = new JPanel(new BorderLayout());
                filePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                String fileType = file.getType();
                ImageIcon icon = null;

                switch(fileType) {
                    case "folder":
                        icon = new ImageIcon(getClass().getResource("/resources/pic/folder.png"));
                        break;
                    case "file":
                        icon = new ImageIcon(getClass().getResource("/resources/pic/file.png"));
                        break;
                    case "image":
                        icon = new ImageIcon(getClass().getResource("/resources/pic/image.png"));
                        break;
                    case "mp3":
                        icon = new ImageIcon(getClass().getResource("/resources/pic/music.png"));
                        break;
                    case "pdf":
                        icon = new ImageIcon(getClass().getResource("/resources/pic/pdf.png"));
                        break;
                    default:
                        icon = new ImageIcon(getClass().getResource("/resources/pic/file.png"));
                }

                JLabel iconLabel = new JLabel(icon);
                JLabel headingLabel = new JLabel(file.getHeading(), JLabel.CENTER);
                JLabel subheadingLabel = new JLabel(file.getSubheading(), JLabel.CENTER);

                filePanel.add(iconLabel, BorderLayout.CENTER);
                filePanel.add(headingLabel, BorderLayout.NORTH);
                filePanel.add(subheadingLabel, BorderLayout.SOUTH);

                // Add click listener
                filePanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        handleFileClick(file);
                    }
                });

                tilePane.add(filePanel);
            }

            tilePane.revalidate();
            tilePane.repaint();
        });
    }

    private void handleFileClick(AvatarFile file) {
        // Handle file/folder click based on file type
        if ("folder".equals(file.getType())) {
            ClientToAndroid.fetchDirectory(file.getPath());
        } else if ("image".equals(file.getType())) {
            // Handle image display
            showImage(file.getHeading(), file.getPath());
        } else {
            // Handle other file types
            showMessage("Selected: " + file.getHeading());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainScreenController controller = new MainScreenController();
            controller.initialize();
        });
    }
}