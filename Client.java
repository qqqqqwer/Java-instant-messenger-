package ChatClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressWarnings("DuplicatedCode")
class Client extends JFrame {

    private boolean isOnline;
    private JTextArea chatWindow;
    private JTextField messageField;
    private String helpMessage;

    private InputStreamReader input;
    private BufferedReader bf;
    private PrintWriter pr;

    private boolean sendMessage;

    private Socket socket;

    private void start(){

        new Thread(() -> {

            sendLocalMessage("Trying to connect to the server...");
            messageField.setEnabled(false);
            try {
                //Client socket
                socket = new Socket("localhost", 5000);
                isOnline = true;
                messageField.setEnabled(true);
                sendLocalMessage("Successfully connected to the server");
                try {

                    input = new InputStreamReader(socket.getInputStream());
                    bf = new BufferedReader(input); //get messages
                    pr = new PrintWriter(socket.getOutputStream(), true); //sends messages


                    while (true) {

                        if (socket.isClosed()) {
                            isOnline = false;
                            break;
                        }

                        String messageToSend = messageField.getText();
                        if (sendMessage)
                            sendOnlineMessage(messageToSend);
                        receiveMessage();
                    }
                    socket = null;


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } catch (Exception e) {
                chatWindow.append("Server is offline \n");
            }




        }).start();


    }

    Client() {

        isOnline = false;

        helpMessage =
                "To send a message type your message in the lower text box and press Enter \n" +
                        "To go online type - /start \n" +
                        "To go offline type - /stop \n" +
                        "To clear chat window type - /clear \n" +
                        "To see this message again type - /help \n";
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BorderLayout(10, 10));

        this.setTitle("Client");
        this.setSize(400, 600);
        this.setResizable(false);
        this.setContentPane(panel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatWindow = new JTextArea();
        chatWindow.setEditable(false);
        chatWindow.setLineWrap(true);
        chatWindow.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(chatWindow, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        messageField = new JTextField();

        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    parseMessage(messageField.getText());

                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(messageField, BorderLayout.SOUTH);

        sendLocalMessage(helpMessage);

    }

    void showWindow() {

        this.setVisible(true);
    }

    private void parseMessage(String message) {

        switch (message.toLowerCase()) {
            case "/start":
                if (!isOnline)
                    start();
                else
                    sendLocalMessage("You are already online. To go offline close this window or type /stop");
                break;
            case "/stop":
                if (isOnline)
                    stop();
                else
                    sendLocalMessage("You are already offline. To go online type /stop");
                break;
            case "/clear":
                chatWindow.setText("");
                break;
            case "/help":
                sendLocalMessage(helpMessage);
                break;
            default:

                if (message.startsWith("/"))
                    sendLocalMessage("Unrecognizable command. Type /help to see all available commands");
                else {
                    if (isOnline)
                        sendMessage = true;
                    else
                        sendLocalMessage("You have to be online to send messages. To go online type /start");
                    break;
                }
        }
        messageField.setText("");
    }

    private void stop() {
        try {
            socket.close();
            isOnline = false;
            sendLocalMessage("You are now offline.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLocalMessage(String message) {
        chatWindow.append(message + "\n");
    }

    private void sendOnlineMessage(String message) {

        pr.println(message);
        if (pr.checkError()) {

            sendLocalMessage("Client is no longer online. Closing the connection.");

            stop();

        }

        sendLocalMessage("You: " + message);
        pr.flush();
        messageField.setText("");
        sendMessage = false;

    }

    private void receiveMessage() throws Exception {

        String line;
        while(bf.ready() && (line = bf.readLine()) != null) {
            sendLocalMessage("Server: " + line);
        }
    }

}
