package org.guercifzone.Gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane tabbedPane;

    public MainWindow() {
        this.setTitle("JavaFishing");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center the window
        this.setVisible(true);
        this.setResizable(false);


        unitUi();
    }

    private void unitUi() {
        tabbedPane = new JTabbedPane();
//panels
      ServerRun serverRun = new ServerRun();
    //  URLCreator urlCreator = new URLCreator();
      LogsView logsViewer = new LogsView();

//tabs
        tabbedPane.addTab("Server Controler",serverRun);
       // tabbedPane.addTab("Url generator",urlCreator);
        tabbedPane.addTab("captur logs",logsViewer);



//components
   setLayout(new BorderLayout());
   add(tabbedPane,BorderLayout.CENTER);

    }

}
