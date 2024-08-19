
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends JFrame {
    public Main(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0));
        this.setSize(200,200);
        this.setLocationRelativeTo(null);

        this.pack();
        this.setVisible(true);
        String open = "{\n";
        String name = " \"First_Name:\"" + " \"Shikhar\",\n";
        String name1 = " \"First_Name:\"" + " \"Shikhar\",\n";
        String name2 = " \"First_Name:\"" + " \"Shikhar\",\n";
        String name3 = " \"First_Name:\"" + " \"Shikhar\",\n";
         String close = "},\n";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.json"));
            bufferedWriter.write(""+open+" "+ name3 +""+name1+""+name2+""+name3+""+close);
            bufferedWriter.write(System.getProperty("line.separator"));
            bufferedWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("JSON file created: ");

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}

