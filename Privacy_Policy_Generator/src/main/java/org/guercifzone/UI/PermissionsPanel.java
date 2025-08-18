package org.guercifzone.UI;

import javax.swing.*;
import java.awt.*;

public class PermissionsPanel extends JPanel {
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