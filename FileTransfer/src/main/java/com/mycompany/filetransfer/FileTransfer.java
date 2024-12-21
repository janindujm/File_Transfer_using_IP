/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.filetransfer;
/*MY INdex =22/ENG/034
  P2 Index =22/ENG/011

*/

/**
 *
 * @author hp
 */








import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileTransfer {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("File Transfer Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Welcome to File Transfer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Center panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 20, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Sender button
        JButton senderButton = new JButton("I am a Sender");
        senderButton.setFont(new Font("Arial", Font.PLAIN, 18));
        senderButton.setBackground(new Color(102, 204, 255));
        senderButton.setFocusPainted(false);
        senderButton.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 204), 2));
        senderButton.setOpaque(true);

        // Receiver button
        JButton receiverButton = new JButton("I am a Receiver");
        receiverButton.setFont(new Font("Arial", Font.PLAIN, 18));
        receiverButton.setBackground(new Color(153, 255, 153));
        receiverButton.setFocusPainted(false);
        receiverButton.setBorder(BorderFactory.createLineBorder(new Color(0, 204, 102), 2));
        receiverButton.setOpaque(true);

        // Chat button
        JButton chatButton = new JButton("Chat");
        chatButton.setFont(new Font("Arial", Font.PLAIN, 18));
        chatButton.setBackground(new Color(255, 204, 102));
        chatButton.setFocusPainted(false);
        chatButton.setBorder(BorderFactory.createLineBorder(new Color(255, 153, 51), 2));
        chatButton.setOpaque(true);

        // Add buttons to the panel
        buttonPanel.add(senderButton);
        buttonPanel.add(receiverButton);
        buttonPanel.add(chatButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Footer panel
        JLabel footerLabel = new JLabel("Select your role to continue.", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerLabel.setForeground(Color.DARK_GRAY);
        frame.add(footerLabel, BorderLayout.SOUTH);

        // Add action listeners to the buttons
        senderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Sender mode selected!", "Sender", JOptionPane.INFORMATION_MESSAGE);
                // Launch the sender GUI
                new SenderGUI().showSenderGUI();
            }
        });

        receiverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Receiver mode selected!", "Receiver", JOptionPane.INFORMATION_MESSAGE);
                // Launch the receiver functionality
                new ReceiverGUI().showReceiverGUI();
            }
        });

        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Chat mode selected!", "Chat", JOptionPane.INFORMATION_MESSAGE);
                // Launch the chat GUI
                SwingUtilities.invokeLater(() -> new livechat().setVisible(true));
            }
        });

        // Display the frame
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}



