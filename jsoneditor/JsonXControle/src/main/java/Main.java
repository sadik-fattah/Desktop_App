
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
        JButton btnLoad,btnSave,btnShow;
        JLabel lblPath,lblimag,lbltitle,lblname,lblurl,lbltype;
        JTextField textJsonPath,textjsonImg,textjsontitle,textjsonname,textjsonurl,textjsontype;
        JTextField imgvalue,titlevalue,namevalue,urlvalue,typevalue;
        JTable mytable;
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Source Code jsonEditor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);



        lblPath = new JLabel("The Path:");
        lblPath.setBounds(40,5,100,20);
        textJsonPath = new JTextField();
        textJsonPath.setBounds(40, 25, 300, 20);
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

        btnLoad = new JButton("...");
        btnLoad.setBounds(350, 25, 60, 20);

        btnShow= new JButton("Show");
        btnShow.setBounds(300, 50, 150, 20);

        btnSave= new JButton("Save");
        btnSave.setBounds(300, 80, 150, 20);
btnLoad.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        int userChoise = jfc.showOpenDialog(null);
        if (userChoise == JFileChooser.APPROVE_OPTION){
            File selectedfile = jfc.getSelectedFile();
            textJsonPath.setText(selectedfile.getPath());
        }
        if (userChoise == JFileChooser.CANCEL_OPTION)
            textJsonPath.setText("no file selected");
    }

});
btnSave.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String open = "{\n";
        String name = " \"First_Name:\"" + " \"Shikhar\",\n";
        String name1 = " \"First_Name:\"" + " \"Shikhar\",\n";
        String name2 = " \"First_Name:\"" + " \"Shikhar\",\n";
        String name3 = " \"First_Name:\"" + " \"Shikhar\",\n";
        String close = "},\n";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(textJsonPath.getText()));
            bufferedWriter.write(""+open+" "+name+""+ name3 +""+name1+""+name2+""+name3+""+close);
            bufferedWriter.write(System.getProperty("line.separator"));
            bufferedWriter.close();
        } catch (IOException er) {
            // TODO Auto-generated catch block
            er.printStackTrace();
        }
    }
});

        System.out.println("JSON file created: ");

        frame.add(btnLoad);
        frame.add(btnShow);
        frame.add(btnSave);

        frame.add(lblPath);
        frame.add(lblname);
        frame.add(lblimag);
        frame.add(lbltitle);
        frame.add(lbltype);

        frame.add(textJsonPath);
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

