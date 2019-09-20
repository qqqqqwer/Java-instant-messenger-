package ChatClient;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.*;
import java.io.*;

class Server extends JFrame{

    private boolean isOnline;
    private JTextArea chatWindow;
    private JTextField messageField;
    private String helpMessage;

    private InputStreamReader input;
    private BufferedReader bf;
    private PrintWriter pr;

    private boolean sendMessage;
    private Socket socket;

    private void start()  {

        new Thread(() -> {
            try {
                sendLocalMessage("Server is online. Waiting for client to join...");
                messageField.setEnabled(false);
                socket = new ServerSocket(5000).accept();
                messageField.setEnabled(true);
                isOnline = true;

                sendLocalMessage("Client connected");

                pr = new PrintWriter(socket.getOutputStream(), true); //sends messages
                input = new InputStreamReader(socket.getInputStream());
                bf = new BufferedReader(input); //get messages


                while (true) {

                    if (socket.isClosed()) {
                        isOnline = false;
                        break;
                    }

                    receiveMessage();

                    String messageToSend = messageField.getText();
                    if (sendMessage && !messageToSend.equals("")) {
                        sendOnlineMessage(messageToSend);

                    }
                }

                socket = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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


    Server() {
        sendMessage = false;
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

        this.setTitle("Server");
        this.setSize(400, 600);
        this.setResizable(false);
        this.setContentPane(panel);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);

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

    private void sendLocalMessage(String message) {
        chatWindow.append(message + "\n");
    }

    private void receiveMessage() throws Exception{

        String line;
        while(bf.ready() && (line = bf.readLine()) != null) {
            sendLocalMessage("Client: " + line);
        }

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

}
