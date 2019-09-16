package ChatClient;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;

public class Server extends JFrame {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5000);
        Socket s = server.accept();

        System.out.println("client connected");
        InputStreamReader input = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(input);

        String message = bf.readLine();
        System.out.println("Client: " + message);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println("Hello from server");
        pr.flush();
    }


}
