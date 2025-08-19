package org.guercifzone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ReciterManager extends JFrame {
    private JsonFileHandler jsonHandler;
    private DefaultListModel<Reciter> listModel;
    private JList<Reciter> reciterList;

    private JTextField nameField;
    private JTextField imageField;
    private JTextField linkField;
    private JTextField descriptionField;

    public ReciterManager() {
        jsonHandler = new JsonFileHandler("/media/jakinzo/USB_PROJECT/SimpleDataBase/60Hizb/9oraa.json");
        initializeUI();
        loadReciters();
    }

    private void initializeUI() {
        setTitle("Quran Reciter Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel for list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));

        listModel = new DefaultListModel<>();
        reciterList = new JList<>(listModel);
        reciterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reciterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedReciter();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(reciterList);
        leftPanel.add(new JLabel("Reciters List:"), BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // Right panel for form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Reciter Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        rightPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        rightPanel.add(nameField, gbc);

        // Image field
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        rightPanel.add(new JLabel("Image URL:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        imageField = new JTextField(20);
        rightPanel.add(imageField, gbc);

        // Link field
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        rightPanel.add(new JLabel("Link URL:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        linkField = new JTextField(20);
        rightPanel.add(linkField, gbc);

        // Description field
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        rightPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        descriptionField = new JTextField(20);
        rightPanel.add(descriptionField, gbc);

        // Buttons panel
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Add New");
        addButton.addActionListener(e -> addNewReciter());

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateReciter());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteReciter());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        rightPanel.add(buttonPanel, gbc);

        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadReciters() {
        List<Reciter> reciters = jsonHandler.readReciters();
        listModel.clear();
        for (Reciter reciter : reciters) {
            listModel.addElement(reciter);
        }
    }

    private void displaySelectedReciter() {
        Reciter selected = reciterList.getSelectedValue();
        if (selected != null) {
            nameField.setText(selected.getName());
            imageField.setText(selected.getImage());
            linkField.setText(selected.getLink());
            descriptionField.setText(selected.getDescription());
        }
    }

    private void addNewReciter() {
        String name = nameField.getText().trim();
        String image = imageField.getText().trim();
        String link = linkField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Reciter newReciter = new Reciter(name, image, link, description);
        jsonHandler.addReciter(newReciter);
        listModel.addElement(newReciter);
        clearForm();

        JOptionPane.showMessageDialog(this, "Reciter added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateReciter() {
        int selectedIndex = reciterList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reciter to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String image = imageField.getText().trim();
        String link = linkField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Reciter updatedReciter = new Reciter(name, image, link, description);
        List<Reciter> reciters = jsonHandler.readReciters();
        reciters.set(selectedIndex, updatedReciter);
        jsonHandler.writeReciters(reciters);

        listModel.set(selectedIndex, updatedReciter);
        JOptionPane.showMessageDialog(this, "Reciter updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteReciter() {
        int selectedIndex = reciterList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reciter to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this reciter?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<Reciter> reciters = jsonHandler.readReciters();
            reciters.remove(selectedIndex);
            jsonHandler.writeReciters(reciters);

            listModel.remove(selectedIndex);
            clearForm();
            JOptionPane.showMessageDialog(this, "Reciter deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        imageField.setText("");
        linkField.setText("");
        descriptionField.setText("");
        reciterList.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ReciterManager manager = new ReciterManager();
            manager.setVisible(true);
        });
    }
}
