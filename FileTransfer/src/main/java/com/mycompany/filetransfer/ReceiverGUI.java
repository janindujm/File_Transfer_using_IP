/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.filetransfer;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverGUI {

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    // GUI Components
    private JFrame frame;
    private JTextField fileNameField;
    private JButton startButton;

    public void showReceiverGUI() {
        // Set up the JFrame (main window)
        frame = new JFrame("Receiver File Transfer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.setSize(300, 150);

        // Add label and text field for file name input
        frame.add(new JLabel("Enter file name to save:"));
        fileNameField = new JTextField(20);
        frame.add(fileNameField);

        // Add Start Button to begin receiving files
        startButton = new JButton("Start Receiving");
        frame.add(startButton);

        // Action listener for the start button
        startButton.addActionListener(e -> {
            String fileName = fileNameField.getText().trim();
            if (fileName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "File name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                startServer(fileName);
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    // Method to start the server and receive the file
    private void startServer(String fileName) {
        try (ServerSocket serverSocket = new ServerSocket(900)) {
            System.out.println("Server is Starting on Port 900");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");

            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            // Call the receiveFile method with the user-input file name
            receiveFile(fileName);

            // Close connections
            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to receive file and save it with the user-specified name
    private static void receiveFile(String fileName) throws Exception {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong(); // Read file size
        byte[] buffer = new byte[4 * 1024]; // Buffer to hold data
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            // Write data to file
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes; // Reduce remaining size
        }
        // File received
        System.out.println("File is Received and saved as: " + fileName);
        fileOutputStream.close();
    }

    public static void main(String[] args) {
        // Create an instance of ReceiverGUI and show the GUI
        ReceiverGUI receiver = new ReceiverGUI();
        receiver.showReceiverGUI();
    }
}

