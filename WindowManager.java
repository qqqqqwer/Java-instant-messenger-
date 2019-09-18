package ChatClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowManager extends JFrame {

    private JButton launchServerButton;
    private JButton launchClientButton;
    private Server server;
    private Client client;

    public static void main(String[] args) {
        new WindowManager();
    }

    private WindowManager() {

        server = new Server();
        client = new Client();
        launchServerButton = new JButton("Run Server");
        launchClientButton = new JButton("Run client");

        addOnClickListeners();

        JPanel panel = new JPanel();

        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(200, 100);
        this.setTitle("");


        panel.add(launchServerButton);
        panel.add(launchClientButton);
        this.add(panel);

        this.setVisible(true);
    }

    private void addOnClickListeners() {
        launchServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                server.showWindow();
            }
        });

        launchClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    client.showWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
