package org.guercifzone.UI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneratePanel extends JPanel {
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
        if (!appInfoPanel.validateFields()) {
            return;
        }

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

        // HTML Header and Styles
        html.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; } ")
                .append("h1 { color: #2c3e50; border-bottom: 1px solid #eee; padding-bottom: 10px; } ")
                .append("h2 { color: #34495e; margin-top: 25px; border-bottom: 1px solid #f5f5f5; padding-bottom: 5px; } ")
                .append("ul { margin-top: 5px; padding-left: 20px; } ")
                .append("li { margin-bottom: 5px; }")
                .append("</style></head><body>");

        // Policy Title
        html.append("<h1>Privacy Policy for ").append(escapeHtml(appName)).append("</h1>");
        html.append("<p><em>Last updated: ").append(date).append("</em></p>");

        // Introduction Section
        html.append("<h2>Introduction</h2>");
        html.append("<p>").append(escapeHtml(developerName))
                .append(" built the ").append(escapeHtml(appName))
                .append(" app").append(appDescription.isEmpty() ? "" : " (" + escapeHtml(appDescription) + ")")
                .append(". This SERVICE is provided by ").append(escapeHtml(developerName))
                .append(" at no cost and is intended for use as is.</p>");
        html.append("<p>This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.</p>");
        html.append("<p>If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.</p>");

        // Information Collection and Use
        if (dataCollectionPanel.collectsAnyData()) {
            html.append("<h2>Information Collection and Use</h2>");
            html.append("<p>For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information. The information that we request will be retained by us and used as described in this privacy policy.</p>");

            html.append("<p>The app uses the following data:</p><ul>");
            if (dataCollectionPanel.collectsEmail()) html.append("<li>Email address</li>");
            if (dataCollectionPanel.collectsName()) html.append("<li>Name</li>");
            if (dataCollectionPanel.collectsPhone()) html.append("<li>Phone number</li>");
            if (dataCollectionPanel.collectsLocation()) html.append("<li>Location data</li>");
            if (dataCollectionPanel.collectsDeviceInfo()) html.append("<li>Device information (model, OS version, etc.)</li>");
            if (dataCollectionPanel.collectsUsageData()) html.append("<li>Usage data (how you interact with the app)</li>");
            html.append("</ul>");
        }

        // Log Data Section
        html.append("<h2>Log Data</h2>");
        html.append("<p>We want to inform you that whenever you use our Service, in a case of an error in the app we collect data and information (through third-party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (\"IP\") address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.</p>");

        // Cookies Section
        if (dataCollectionPanel.usesCookies()) {
            html.append("<h2>Cookies</h2>");
            html.append("<p>Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.</p>");
            html.append("<p>This Service does not use these \"cookies\" explicitly. However, the app may use third-party code and libraries that use \"cookies\" to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.</p>");
        }

        // Service Providers Section
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

        // Security Section
        html.append("<h2>Security</h2>");
        html.append("<p>We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.</p>");

        // Links to Other Sites Section
        html.append("<h2>Links to Other Sites</h2>");
        html.append("<p>This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy Policy of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.</p>");

        // Children's Privacy Section
        html.append("<h2>Children's Privacy</h2>");
        html.append("<p>These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do the necessary actions.</p>");

        // Changes to This Privacy Policy
        html.append("<h2>Changes to This Privacy Policy</h2>");
        html.append("<p>We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page.</p>");
        html.append("<p>This policy is effective as of ").append(date).append("</p>");

        // Contact Us Section
        html.append("<h2>Contact Us</h2>");
        html.append("<p>If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at ").append(escapeHtml(contactEmail)).append(".");
        if (!website.isEmpty()) {
            html.append(" You may also visit our website at ").append(escapeHtml(website)).append(".");
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

        StringSelection selection = new StringSelection(policyText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);

        JOptionPane.showMessageDialog(this,
                "Policy text copied to clipboard!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}