/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.filetransfer;






import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Base64;

public class livechat extends JFrame {
    // Networking components
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    // File transfer components
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    
    // GUI Components
    private JTextField ipAddressField;
    private JTextArea chatArea;
    private JTextArea messageArea;
    private JButton connectButton;
    private JButton sendButton;
    private JButton attachButton;
    private JButton acceptButton;
    private JButton attachAcceptButton;
    private JLabel fileNameLabel;
    private JCheckBox secureConnectionCheckBox;
    
    // Encryption components
    private boolean isSecureMode = false;
    private static final String SECRET_KEY = "P2P_CHAT_SECRET_KEY_32CHARS_LONG!!!";
    
    public livechat() {
        initializeGUI();
        setupNetworkingComponents();
    }
    
    private void initializeGUI() {
        setTitle("P2P Chat & File Sharing");
        setSize(600, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Top Panel for IP and Connection
        JPanel connectionPanel = new JPanel(new FlowLayout());
        ipAddressField = new JTextField(15);
        ipAddressField.setToolTipText("Enter Remote IP Address");
        connectButton = new JButton("Connect");
        acceptButton = new JButton("Accept Connection");
        secureConnectionCheckBox = new JCheckBox("Secure Communication");
        
        connectionPanel.add(new JLabel("IP:"));
        connectionPanel.add(ipAddressField);
        connectionPanel.add(connectButton);
        connectionPanel.add(acceptButton);
        connectionPanel.add(secureConnectionCheckBox);
        
        // Chat Areas
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        messageArea = new JTextArea(3, 40);
        
        // Send Panel
        JPanel sendPanel = new JPanel(new BorderLayout());
        sendButton = new JButton("Send");
        attachButton = new JButton("Attach File");
        attachAcceptButton = new JButton("Accept File");
        fileNameLabel = new JLabel("No file selected");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(attachButton);
        buttonPanel.add(attachAcceptButton);
        
        sendPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        sendPanel.add(buttonPanel, BorderLayout.SOUTH);
        sendPanel.add(fileNameLabel, BorderLayout.NORTH);
        
        // Add components to frame
        add(connectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(sendPanel, BorderLayout.SOUTH);
        
        // Event Listeners
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        connectButton.addActionListener(e -> connectToRemoteHost());
        acceptButton.addActionListener(e -> startServerAndListen());
        sendButton.addActionListener(e -> sendMessage());
        attachButton.addActionListener(e -> attachFile());
        attachAcceptButton.addActionListener(e -> acceptIncomingFile());
    }
    
    private void setupNetworkingComponents() {
        try {
            // Initialize default server socket on a specific port
            serverSocket = new ServerSocket(9876);
        } catch (IOException e) {
            showError("Error setting up network: " + e.getMessage());
        }
    }
    
    private void connectToRemoteHost() {
        String ipAddress = ipAddressField.getText();
        try {
            socket = new Socket(ipAddress, 9876);
            setupIOStreams();
            chatArea.append("Connected to " + ipAddress + "\n");
        } catch (IOException e) {
            showError("Connection Failed: " + e.getMessage());
        }
    }
    
    private void startServerAndListen() {
        new Thread(() -> {
            try {
                socket = serverSocket.accept();
                setupIOStreams();
                chatArea.append("Incoming connection accepted\n");
            } catch (IOException e) {
                showError("Connection Accept Failed: " + e.getMessage());
            }
        }).start();
    }
    
    private void setupIOStreams() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());
            
            // Start listening thread
            startListeningThread();
        } catch (IOException e) {
            showError("IO Stream Setup Failed: " + e.getMessage());
        }
    }
    
    private void startListeningThread() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // If secure mode is on, decrypt message
                    if (isSecureMode) {
                        //message = decrypt(message);
                    }
                    chatArea.append("Received: " + message + "\n");
                }
            } catch (IOException e) {
                showError("Listening Thread Error: " + e.getMessage());
            }
        }).start();
    }
    
    private void sendMessage() {
        String message = messageArea.getText();
        if (message.isEmpty()) return;
        
        try {
            // If secure mode is on, encrypt message
            if (isSecureMode) {
                message = encrypt(message);
            }
            
            out.println(message);
            chatArea.append("Sent: " + messageArea.getText() + "\n");
            messageArea.setText("");
        } catch (Exception e) {
            showError("Send Message Failed: " + e.getMessage());
        }
    }
    
    private void attachFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Check file size (less than 1MB)
            if (selectedFile.length() > 1024 * 1024) {
                showError("File too large. Maximum 1MB allowed.");
                return;
            }
            
            fileNameLabel.setText(selectedFile.getName());
            sendFile(selectedFile);
        }
    }
    
    private void sendFile(File file) {
        new Thread(() -> {
            try {
                // Send file name first
                dataOut.writeUTF(file.getName());
                
                // Send file content
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, bytesRead);
                    }
                }
                
                chatArea.append("File sent: " + file.getName() + "\n");
            } catch (IOException e) {
                showError("File Send Failed: " + e.getMessage());
            }
        }).start();
    }
    
    private void acceptIncomingFile() {
        new Thread(() -> {
            try {
                // Receive file name
                String fileName = dataIn.readUTF();
                File saveFile = new File(System.getProperty("user.home") + "/Downloads/" + fileName);
                
                try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = dataIn.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                
                chatArea.append("File received: " + fileName + "\n");
            } catch (IOException e) {
                showError("File Receive Failed: " + e.getMessage());
            }
        }).start();
    }
    
    // Simple AES Encryption Methods
    private String encrypt(String strToEncrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
    }
    
    private String decrypt(String strToDecrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new livechat().setVisible(true);
    });
    }
}

