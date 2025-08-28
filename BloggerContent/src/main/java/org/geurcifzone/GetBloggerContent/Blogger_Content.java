package org.geurcifzone.GetBloggerContent;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
public class Blogger_Content {
    private static JPanel jpanel1;
    private static JLabel label1;
   private static JTextField bloggerLinkTextField;
  //  private static MyTextView bloggerLinkTextField;
    private static JButton button1;
private static void CreatGuiDesing(){
    JFrame frame = new JFrame("Blogger Content");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem pasteItem = new JMenuItem("paste");
    jpanel1 = new JPanel(new BorderLayout());
    label1 = new JLabel("Blogger Link :");
    button1 = new JButton("Created");
    bloggerLinkTextField = new JTextField(50);

    jpanel1.add(label1, BorderLayout.WEST);
    jpanel1.add(bloggerLinkTextField, BorderLayout.CENTER);
    jpanel1.add(button1, BorderLayout.EAST);

    frame.add(jpanel1, BorderLayout.CENTER);

    popupMenu.add(pasteItem);


    bloggerLinkTextField.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            if (e.isPopupTrigger()){
                popupMenu.show(e.getComponent(),e.getX(),e.getY());
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (e.isPopupTrigger()){
                popupMenu.show(e.getComponent(),e.getX(),e.getY());
            }
        }
    });
    pasteItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bloggerLinkTextField.paste();
        }
    });
    button1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            RunInBack();
        }
    });


    frame.pack();
    frame.setVisible(true);
}
public static void RunInBack(){
    String feedUrl = bloggerLinkTextField.getText().toString().trim() + "feeds/posts/default?alt=rss";
    String outputFile = "output.xml";

if (bloggerLinkTextField.getText().isEmpty()){
    JOptionPane.showConfirmDialog(null,
            "Put a blog link in the field.\n" + "It is better to have a blogger blogspot\n" + "Because the result will be an xml file..",
            "No Data",
            JOptionPane.PLAIN_MESSAGE);
}
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
        "Feed data saved to " + outputFile,
        "Sucsses",
        JOptionPane.PLAIN_MESSAGE);
         //   System.out.println("Feed data saved to " + outputFile);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch feed. HTTP Response Code:" + responseCode,
                    "Failed",
                    JOptionPane.PLAIN_MESSAGE);
         //   System.out.println("Failed to fetch feed. HTTP Response Code: " + responseCode);
        }
    }catch (Exception e){
        e.printStackTrace();
    }
}
public static void main(String[] args) {
        CreatGuiDesing();


    }
}
