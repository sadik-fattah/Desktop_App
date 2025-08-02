package org.guercifzone.chatgpt;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.cos.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Base64;

public class PDF2JohnSwing extends JFrame {

    private JTextArea outputArea;

    public PDF2JohnSwing() {
        setTitle("PDF2John GUI (Java)");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton openButton = new JButton("Select Encrypted PDF");
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        openButton.addActionListener(this::handleOpenPDF);

        add(openButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleOpenPDF(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File pdfFile = chooser.getSelectedFile();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            if (!document.isEncrypted()) {
                outputArea.setText("PDF is not encrypted.");
                return;
            }

            COSDictionary encryption = document.getEncryption().getCOSObject();

            COSString o = (COSString) encryption.getDictionaryObject(COSName.O);
            COSString u = (COSString) encryption.getDictionaryObject(COSName.U);
            int p = ((COSInteger) encryption.getDictionaryObject(COSName.P)).intValue();
            int v = ((COSInteger) encryption.getDictionaryObject(COSName.V)).intValue();
            int r = ((COSInteger) encryption.getDictionaryObject(COSName.R)).intValue();
            int length = encryption.containsKey(COSName.LENGTH) ?
                    ((COSInteger) encryption.getDictionaryObject(COSName.LENGTH)).intValue() : 40;

            // Attempt to get ID from trailer (needs raw COS access)
            COSArray idArray = (COSArray) document.getDocument().getTrailer().getDictionaryObject(COSName.ID);
            COSString id = idArray != null ? (COSString) idArray.get(0) : null;

            // Construct hash in $pdf$ format
            String hash = String.format(
                    "$pdf$%d*%d*%d*%d*%d*%d*%s*%s*%s",
                    v,
                    length,
                    p,
                    r,
                    o.getBytes().length,
                    u.getBytes().length,
                    bytesToHex(o.getBytes()),
                    bytesToHex(u.getBytes()),
                    (id != null ? bytesToHex(id.getBytes()) : "0")
            );

            outputArea.setText("PDF Hash for JtR:\n\n" + hash);

        } catch (Exception ex) {
            ex.printStackTrace();
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b & 0xFF));
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PDF2JohnSwing().setVisible(true));
    }
}
