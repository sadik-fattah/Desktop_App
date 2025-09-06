package org.guercifzone;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main  extends JFrame{
    public Main(){
        initComponents();
    }
    private void initComponents(){
        
    }
    public static void main(String[] args) {

       try {
           for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
               if ("Nimbus".equals(info.getName())){
                   UIManager.setLookAndFeel(info.getClassName());
                   break;
               }
           }

       }catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
               UnsupportedLookAndFeelException e){
           Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
       }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

}
