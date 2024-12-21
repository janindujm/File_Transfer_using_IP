/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.filetransfer;

/**
 *
 * @author hp
 */
import javax.swing.*; // For GUI components like JFrame, JPanel, JLabel, JButton, JOptionPane
import java.awt.*; // For layout and color settings
import java.awt.event.ActionEvent; // For action events
import java.awt.event.ActionListener; // For handling button clicks
import java.io.*; // For file handling and streams
import java.net.*; // For socket communication
import java.awt.datatransfer.DataFlavor; // For drag-and-drop
import java.awt.datatransfer.Transferable; // For file transfer data
import java.awt.dnd.*; // For drag-and-drop functionality
import java.util.List; // For handling multiple dropped files





import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/*class SenderGUI {
    private DataOutputStream dataOutputStream = null;

    public void showSenderGUI() {
        SwingUtilities.invokeLater(() -> {*/

public class SenderGUI {

    private JFrame mainFrame;
    private JTextField ipInputField;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;



    public void showSenderGUI() {
        
        // Main Frame setup
        mainFrame = new JFrame("Sender - Peer to Peer Chat");
        mainFrame.setSize(400, 200);
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title Label
        JLabel titleLabel = new JLabel("SENDER - PEER TO PEER CHAT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(70, 20, 300, 20);
        mainFrame.add(titleLabel);

        // IP Address Input
        JLabel ipLabel = new JLabel("Receiver's IP Address: ");
        ipLabel.setBounds(50, 60, 150, 25);
        mainFrame.add(ipLabel);

        ipInputField = new JTextField();
        ipInputField.setBounds(200, 60, 150, 25);
        mainFrame.add(ipInputField);

        // Connect Button
        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(150, 110, 100, 30);
        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.WHITE);
        mainFrame.add(connectButton);

        // Connect Button Action
        connectButton.addActionListener(e -> connectToReceiver());

        mainFrame.setVisible(true);
    }
                

    private void connectToReceiver() {
        String ipAddress = ipInputField.getText().trim();
        int port = 900; // Port should match the receiver's port

        try {
            socket = new Socket(ipAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JOptionPane.showMessageDialog(mainFrame, "Connected to Receiver!", "Connection Status", JOptionPane.INFORMATION_MESSAGE);
            openChatWindow();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Failed to Connect to Receiver!", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openChatWindow() {
        JFrame chatFrame = new JFrame("Sender Chat Window");
        chatFrame.setSize(700, 500);
        chatFrame.setLayout(null);
        chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Sending Text Area
        JLabel sendLabel = new JLabel("Type your message:");
        sendLabel.setBounds(20, 20, 200, 20);
        chatFrame.add(sendLabel);

        JTextArea textSendArea = new JTextArea();
        JScrollPane sendScroll = new JScrollPane(textSendArea);
        sendScroll.setBounds(20, 50, 500, 50);
        chatFrame.add(sendScroll);

        // Send Button
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(540, 50, 100, 50);
        chatFrame.add(sendButton);

        sendButton.addActionListener(e -> {
            String message = textSendArea.getText().trim();
            if (!message.isEmpty()) {
                out.println(message); // Send the message to the receiver
                textSendArea.setText("");
                JOptionPane.showMessageDialog(chatFrame, "Message Sent!", "Status", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // File Sender with Drag-and-Drop
        JLabel fileDragLabel = new JLabel("Drag and Drop File Here:");
        fileDragLabel.setBounds(20, 150, 200, 20);
        chatFrame.add(fileDragLabel);

        JTextArea fileDropArea = new JTextArea("Drag a file here to send.");
        fileDropArea.setBackground(Color.LIGHT_GRAY);
        fileDropArea.setBounds(20, 180, 620, 50);
        fileDropArea.setEditable(false);
        fileDropArea.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<?> droppedFiles = (List<?>) evt.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    for (Object file : droppedFiles) {
                        File f = (File) file;
                        fileDropArea.setText("File sent: " + f.getName());
                        sendFile(f); // Call the sendFile method
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        chatFrame.add(fileDropArea);

        chatFrame.setVisible(true);
    }

    private void sendFile(File file) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             OutputStream os = socket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            JOptionPane.showMessageDialog(null, "File Sent Successfully!", "File Transfer", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File Transfer Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SenderGUI::new);
    }
}

