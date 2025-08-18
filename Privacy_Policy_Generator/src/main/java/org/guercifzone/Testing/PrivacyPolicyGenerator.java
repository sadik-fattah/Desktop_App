package org.guercifzone.Testing;


/**
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivacyPolicyGenerator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private AppInfoPanel appInfoPanel;
    private PermissionsPanel permissionsPanel;
    private DataCollectionPanel dataCollectionPanel;
    private GeneratePanel generatePanel;

    public MainFrame() {
        setTitle("Privacy Policy Generator");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        appInfoPanel = new AppInfoPanel();
        permissionsPanel = new PermissionsPanel();
        dataCollectionPanel = new DataCollectionPanel();
        generatePanel = new GeneratePanel(appInfoPanel, permissionsPanel, dataCollectionPanel);
    }

    private void layoutComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. App Info", appInfoPanel);
        tabbedPane.addTab("2. Permissions", permissionsPanel);
        tabbedPane.addTab("3. Data Collection", dataCollectionPanel);
        tabbedPane.addTab("4. Generate Policy", generatePanel);

        add(tabbedPane);
    }
}

class AppInfoPanel extends JPanel {
    private JTextField appNameField;
    private JTextField developerNameField;
    private JTextField contactEmailField;
    private JTextField websiteField;
    private JTextArea appDescriptionArea;

    public AppInfoPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // App Name
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("App Name:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        appNameField = new JTextField(30);
        add(appNameField, gbc);

        // Developer Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Developer Name:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        developerNameField = new JTextField(30);
        add(developerNameField, gbc);

        // Contact Email
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Contact Email:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        contactEmailField = new JTextField(30);
        add(contactEmailField, gbc);

        // Website
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Website (optional):"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        websiteField = new JTextField(30);
        add(websiteField, gbc);

        // App Description
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("App Description:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridy = 4;
        appDescriptionArea = new JTextArea(5, 30);
        appDescriptionArea.setLineWrap(true);
        appDescriptionArea.setWrapStyleWord(true);
        add(new JScrollPane(appDescriptionArea), gbc);

        // Add some padding at the bottom
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    public String getAppName() {
        return appNameField.getText().trim();
    }

    public String getDeveloperName() {
        return developerNameField.getText().trim();
    }

    public String getContactEmail() {
        return contactEmailField.getText().trim();
    }

    public String getWebsite() {
        return websiteField.getText().trim();
    }

    public String getAppDescription() {
        return appDescriptionArea.getText().trim();
    }
}

class PermissionsPanel extends JPanel {
    private JCheckBox internetCheckBox;
    private JCheckBox storageCheckBox;
    private JCheckBox locationCheckBox;
    private JCheckBox cameraCheckBox;
    private JCheckBox microphoneCheckBox;
    private JCheckBox contactsCheckBox;

    public PermissionsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Select permissions your app requires:");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        add(titleLabel);
        add(Box.createVerticalStrut(15));

        // Checkboxes
        internetCheckBox = createPermissionCheckbox("Internet Access", "Required for network operations");
        storageCheckBox = createPermissionCheckbox("Storage Access", "Read/write files on device");
        locationCheckBox = createPermissionCheckbox("Location Access", "Access device location");
        cameraCheckBox = createPermissionCheckbox("Camera Access", "Take pictures or video");
        microphoneCheckBox = createPermissionCheckbox("Microphone Access", "Record audio");
        contactsCheckBox = createPermissionCheckbox("Contacts Access", "Read device contacts");

        add(internetCheckBox);
        add(storageCheckBox);
        add(locationCheckBox);
        add(cameraCheckBox);
        add(microphoneCheckBox);
        add(contactsCheckBox);

        add(Box.createVerticalGlue());
    }

    private JCheckBox createPermissionCheckbox(String text, String tooltip) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setToolTipText(tooltip);
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return checkBox;
    }

    public boolean hasInternetPermission() {
        return internetCheckBox.isSelected();
    }

    public boolean hasStoragePermission() {
        return storageCheckBox.isSelected();
    }

    public boolean hasLocationPermission() {
        return locationCheckBox.isSelected();
    }

    public boolean hasCameraPermission() {
        return cameraCheckBox.isSelected();
    }

    public boolean hasMicrophonePermission() {
        return microphoneCheckBox.isSelected();
    }

    public boolean hasContactsPermission() {
        return contactsCheckBox.isSelected();
    }

    public boolean hasAnyPermission() {
        return hasInternetPermission() || hasStoragePermission() || hasLocationPermission() ||
                hasCameraPermission() || hasMicrophonePermission() || hasContactsPermission();
    }
}

class DataCollectionPanel extends JPanel {
    private JCheckBox collectsEmailCheckBox;
    private JCheckBox collectsNameCheckBox;
    private JCheckBox collectsPhoneCheckBox;
    private JCheckBox collectsLocationCheckBox;
    private JCheckBox collectsUsageDataCheckBox;
    private JCheckBox collectsDeviceInfoCheckBox;
    private JCheckBox usesCookiesCheckBox;
    private JCheckBox usesThirdPartyServicesCheckBox;

    public DataCollectionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Select what data your app collects:");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        add(titleLabel);
        add(Box.createVerticalStrut(15));

        // Checkboxes
        collectsEmailCheckBox = createDataCheckbox("Email Address", "Collects user's email address");
        collectsNameCheckBox = createDataCheckbox("Name", "Collects user's name");
        collectsPhoneCheckBox = createDataCheckbox("Phone Number", "Collects user's phone number");
        collectsLocationCheckBox = createDataCheckbox("Location Data", "Collects user's location data");
        collectsUsageDataCheckBox = createDataCheckbox("Usage Data", "Collects how user interacts with the app");
        collectsDeviceInfoCheckBox = createDataCheckbox("Device Information", "Collects device model, OS version, etc.");
        usesCookiesCheckBox = createDataCheckbox("Uses Cookies", "App uses cookies or similar technologies");
        usesThirdPartyServicesCheckBox = createDataCheckbox("Third-Party Services", "App uses analytics, ads, or other third-party services");

        add(collectsEmailCheckBox);
        add(collectsNameCheckBox);
        add(collectsPhoneCheckBox);
        add(collectsLocationCheckBox);
        add(collectsUsageDataCheckBox);
        add(collectsDeviceInfoCheckBox);
        add(usesCookiesCheckBox);
        add(usesThirdPartyServicesCheckBox);

        add(Box.createVerticalGlue());
    }

    private JCheckBox createDataCheckbox(String text, String tooltip) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setToolTipText(tooltip);
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return checkBox;
    }

    public boolean collectsEmail() {
        return collectsEmailCheckBox.isSelected();
    }

    public boolean collectsName() {
        return collectsNameCheckBox.isSelected();
    }

    public boolean collectsPhone() {
        return collectsPhoneCheckBox.isSelected();
    }

    public boolean collectsLocation() {
        return collectsLocationCheckBox.isSelected();
    }

    public boolean collectsUsageData() {
        return collectsUsageDataCheckBox.isSelected();
    }

    public boolean collectsDeviceInfo() {
        return collectsDeviceInfoCheckBox.isSelected();
    }

    public boolean usesCookies() {
        return usesCookiesCheckBox.isSelected();
    }

    public boolean usesThirdPartyServices() {
        return usesThirdPartyServicesCheckBox.isSelected();
    }

    public boolean collectsAnyData() {
        return collectsEmail() || collectsName() || collectsPhone() || collectsLocation() ||
                collectsUsageData() || collectsDeviceInfo() || usesCookies() || usesThirdPartyServices();
    }
}

class GeneratePanel extends JPanel {
    private JEditorPane previewPane;
    private JButton generateButton;
    private JButton saveButton;
    private JButton copyButton;
    private AppInfoPanel appInfoPanel;
    private PermissionsPanel permissionsPanel;
    private DataCollectionPanel dataCollectionPanel;

    public GeneratePanel(AppInfoPanel appInfoPanel, PermissionsPanel permissionsPanel, DataCollectionPanel dataCollectionPanel) {
        this.appInfoPanel = appInfoPanel;
        this.permissionsPanel = permissionsPanel;
        this.dataCollectionPanel = dataCollectionPanel;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        previewPane = new JEditorPane();
        previewPane.setContentType("text/html");
        previewPane.setEditable(false);

        generateButton = new JButton("Generate Policy");
        saveButton = new JButton("Save to File");
        copyButton = new JButton("Copy to Clipboard");

        generateButton.addActionListener(e -> generatePolicy());
        saveButton.addActionListener(e -> savePolicy());
        copyButton.addActionListener(e -> copyToClipboard());
    }

    private void layoutComponents() {
        // Preview pane
        add(new JScrollPane(previewPane), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(generateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(copyButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generatePolicy() {
        // Validate required fields
        if (appInfoPanel.getAppName().isEmpty() || appInfoPanel.getDeveloperName().isEmpty() ||
                appInfoPanel.getContactEmail().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields in the 'App Info' tab.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Generate the policy HTML
        String policyHtml = generatePolicyHtml();
        previewPane.setText(policyHtml);
    }

    private String generatePolicyHtml() {
        StringBuilder html = new StringBuilder();
        String appName = appInfoPanel.getAppName();
        String developerName = appInfoPanel.getDeveloperName();
        String contactEmail = appInfoPanel.getContactEmail();
        String website = appInfoPanel.getWebsite();
        String appDescription = appInfoPanel.getAppDescription();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Header
        html.append("<html><head><style>body { font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; } ")
                .append("h1 { color: #2c3e50; } h2 { color: #34495e; margin-top: 20px; } ")
                .append("ul { margin-top: 5px; } li { margin-bottom: 5px; }</style></head><body>");

        // Title
        html.append("<h1>Privacy Policy for ").append(escapeHtml(appName)).append("</h1>");
        html.append("<p>Last updated: ").append(date).append("</p>");

        // Introduction
        html.append("<p>").append(escapeHtml(developerName))
                .append(" built the ").append(escapeHtml(appName))
                .append(" app").append(appDescription.isEmpty() ? "" : " as " + escapeHtml(appDescription))
                .append(". This SERVICE is provided by ").append(escapeHtml(developerName))
                .append(" at no cost and is intended for use as is.</p>");
        html.append("<p>This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.</p>");
        html.append("<p>If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.</p>");

        // Information Collection and Use
        if (dataCollectionPanel.collectsAnyData()) {
            html.append("<h2>Information Collection and Use</h2>");
            html.append("<p>For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information, including but not limited to:</p><ul>");

            if (dataCollectionPanel.collectsEmail()) html.append("<li>Email address</li>");
            if (dataCollectionPanel.collectsName()) html.append("<li>Name</li>");
            if (dataCollectionPanel.collectsPhone()) html.append("<li>Phone number</li>");
            if (dataCollectionPanel.collectsLocation()) html.append("<li>Location data</li>");
            if (dataCollectionPanel.collectsDeviceInfo()) html.append("<li>Device information (such as model, OS version)</li>");

            html.append("</ul>");
            html.append("<p>The information that we request will be retained by us and used as described in this privacy policy.</p>");
        }

        // Log Data
        html.append("<h2>Log Data</h2>");
        html.append("<p>We want to inform you that whenever you use our Service, in a case of an error in the app we collect data and information (through third-party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (\"IP\") address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.</p>");

        // Cookies
        if (dataCollectionPanel.usesCookies()) {
            html.append("<h2>Cookies</h2>");
            html.append("<p>Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.</p>");
            html.append("<p>This Service does not use these \"cookies\" explicitly. However, the app may use third-party code and libraries that use \"cookies\" to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.</p>");
        }

        // Service Providers
        if (dataCollectionPanel.usesThirdPartyServices()) {
            html.append("<h2>Service Providers</h2>");
            html.append("<p>We may employ third-party companies and individuals due to the following reasons:</p><ul>");
            html.append("<li>To facilitate our Service;</li>");
            html.append("<li>To provide the Service on our behalf;</li>");
            html.append("<li>To perform Service-related services; or</li>");
            html.append("<li>To assist us in analyzing how our Service is used.</li>");
            html.append("</ul>");
            html.append("<p>We want to inform users of this Service that these third parties have access to their Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.</p>");
        }

        // Security
        html.append("<h2>Security</h2>");
        html.append("<p>We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.</p>");

        // Links to Other Sites
        html.append("<h2>Links to Other Sites</h2>");
        html.append("<p>This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy Policy of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.</p>");

        // Children's Privacy
        html.append("<h2>Children's Privacy</h2>");
        html.append("<p>These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do the necessary actions.</p>");

        // Changes to This Privacy Policy
        html.append("<h2>Changes to This Privacy Policy</h2>");
        html.append("<p>We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page.</p>");
        html.append("<p>This policy is effective as of ").append(date).append("</p>");

        // Contact Us
        html.append("<h2>Contact Us</h2>");
        html.append("<p>If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at ").append(escapeHtml(contactEmail)).append(".");
        if (!website.isEmpty()) {
            html.append(" or visit our website at ").append(escapeHtml(website)).append(".");
        }
        html.append("</p>");

        html.append("</body></html>");
        return html.toString();
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void savePolicy() {
        if (previewPane.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please generate the policy first.",
                    "No Policy Generated",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Privacy Policy");
        fileChooser.setSelectedFile(new File("privacy_policy.html"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html", "htm"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Ensure .html extension
            if (!file.getName().toLowerCase().endsWith(".html") &&
                    !file.getName().toLowerCase().endsWith(".htm")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(previewPane.getText());
                JOptionPane.showMessageDialog(this,
                        "Policy saved successfully to:\n" + file.getAbsolutePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void copyToClipboard() {
        if (previewPane.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please generate the policy first.",
                    "No Policy Generated",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String policyText = previewPane.getText();
        policyText = policyText.replaceAll("<[^>]*>", ""); // Remove HTML tags
        policyText = policyText.replaceAll("&[^;]*;", ""); // Remove HTML entities

        java.awt.Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(policyText), null);

        JOptionPane.showMessageDialog(this,
                "Policy text copied to clipboard!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}*/