package ChatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 5000);

        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println("Hello");
        pr.flush();

        InputStreamReader input = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(input);

        String message = bf.readLine();
        System.out.println("Server: " + message);

    }
}
