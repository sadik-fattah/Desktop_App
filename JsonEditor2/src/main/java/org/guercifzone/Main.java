package org.guercifzone;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends Component {
    public static void main(String[]args){
        JButton btnSave,btnShow;
        JLabel lblimag,lbltitle,lblname,lblurl,lbltype;
        JTextField textjsonImg,textjsontitle,textjsonname,textjsonurl,textjsontype;
        JTextField imgvalue,titlevalue,namevalue,urlvalue,typevalue;
        JTable mytable;
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Source Code jsonEditor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);



        //data form

        lblimag = new JLabel("Image Value :");
        lblimag.setBounds(40,45,100,20);
        textjsonImg = new JTextField();
        textjsonImg.setBounds(40, 65, 100, 20);
        imgvalue = new JTextField();
        imgvalue.setBounds(150,65,100,20);

        lbltitle = new JLabel("Title Value:");
        lbltitle.setBounds(40,85,100,20);
        textjsontitle = new JTextField();
        textjsontitle.setBounds(40, 105, 100, 20);
        titlevalue = new JTextField();
        titlevalue.setBounds(150,105,100,20);

        lblname = new JLabel("Name Value:");
        lblname.setBounds(40,125,100,20);
        textjsonname = new JTextField();
        textjsonname.setBounds(40, 145, 100, 20);
        namevalue = new JTextField();
        namevalue.setBounds(150,145,100,20);

        lblurl = new JLabel("Url Value");
        lblurl.setBounds(40,145,100,20);
        textjsonurl = new JTextField();
        textjsonurl.setBounds(40, 165, 100, 20);
        urlvalue = new JTextField();
        urlvalue.setBounds(150,165,100,20);

        lbltype = new JLabel("Type Value:");
        lbltype.setBounds(40,165,100,20);
        textjsontype = new JTextField();
        textjsontype.setBounds(40, 185, 100, 20);
        typevalue = new JTextField();
        typevalue.setBounds(150,185,100,20);

        btnShow= new JButton("Show");
        btnShow.setBounds(300, 50, 150, 20);

        btnSave= new JButton("Save");
        btnSave.setBounds(300, 80, 150, 20);

        try {
            FileWriter fileWriter = new FileWriter("output.json");
            fileWriter.write("{\n"+
                    "\""+textjsonImg.getText()+"\": "+"\""+imgvalue.getText() +"\",\n"+
                    "\""+textjsontitle.getText() +"\":"+"\""+titlevalue.getText()+"\",\n"+
                    "\""+textjsonname.getText() +"\":"+"\""+namevalue.getText()+"\",\n"+
                    "\""+textjsonurl.getText() +"\":"+"\""+urlvalue.getText()+"\",\n"+
                    "\""+textjsontype.getText() +"\":"+"\""+typevalue.getText()+"\",\n"+
                    "},");

        } catch (IOException er) {
            // TODO Auto-generated catch block
            er.printStackTrace();
        }


        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        System.out.println("JSON file created: ");


        frame.add(btnShow);
        frame.add(btnSave);


        frame.add(lblname);
        frame.add(lblimag);
        frame.add(lbltitle);
        frame.add(lbltype);


        frame.add(lblimag);
        frame.add(textjsonImg);
        frame.add(textjsonname);
        frame.add(textjsontitle);
        frame.add(textjsontype);

        frame.add(imgvalue);
        frame.add(namevalue);
        frame.add(titlevalue);
        frame.add(typevalue);

        frame.setSize(500, 500);
        frame.setVisible(true);
    }


}

