package org.guercifzone.UI;

import javax.swing.*;
import java.awt.*;

public class DataCollectionPanel extends JPanel {
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