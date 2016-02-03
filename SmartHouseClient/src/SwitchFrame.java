
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tsoglani
 */
public class SwitchFrame extends JFrame {

    private static String commandsMode = "Command Mode";
    private static String outputsMode = "Output Mode";

    private DatagramSocket clientSocket;
    private MenuFrame menuFrame;
    private ArrayList<String> commandList = new ArrayList<String>();
    private ArrayList<String> outputList = new ArrayList<String>();
    private boolean isOnCommandMode = true;
    private JPanel editPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private String usingMode;

    public SwitchFrame(MenuFrame menuFrame) {
        this.menuFrame = menuFrame;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton back = new JButton("Back");
        JButton refresh = new JButton("Refresh");
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JToggleButton modeToggle = new JToggleButton(commandsMode);
        editPanel.add(back);
        editPanel.add(modeToggle);
        editPanel.add(refresh);
        add(editPanel, BorderLayout.BEFORE_FIRST_LINE);
        add(centerPanel);
        usingMode = modeToggle.getText();

        sendForReceiving();

        modeToggle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (modeToggle.getText().equals(commandsMode)) {
                    modeToggle.setText(outputsMode);
                } else if (modeToggle.getText().equals(outputsMode)) {
                    modeToggle.setText(commandsMode);
                }
                usingMode = modeToggle.getText();
                sendForReceiving();
            }
        });
        back.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                menuFrame.setVisible(true);
            }
        });
        
        refresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                sendForReceiving();
            }
        });
        setSize(400, 700);
        setVisible(true);
    }

    private void sendData(final String sendData, final InetAddress IPAddress, final int port) {
        receiver();
        new Thread() {
            @Override
            public void run() {
                try {

                    DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes("UTF-8"), sendData.length(), IPAddress, port);
                    if (clientSocket == null || clientSocket.isClosed()) {
                        clientSocket = new DatagramSocket();
                    }

                    clientSocket.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void sendForReceiving() {
        for (InetAddress inetAddress : AutoConnection.usingInetAddress) {
            if (usingMode.equals(outputsMode)) {
                sendData("getAllOutput", inetAddress, AutoConnection.port);
            } else if (usingMode.equals(commandsMode)) {
                sendData("getAllCommandsOutput", inetAddress, AutoConnection.port);

            }
        }
    }

    private void receiver() {
        new Thread() {
            @Override
            public void run() {

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    if (clientSocket == null || clientSocket.isClosed()) {
                        clientSocket = new DatagramSocket();
                    }
                    clientSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

                    processString(sentence);
                } catch (SocketException e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void processString(String input) {
        String[] pinax = input.split("@@@");
        centerPanel.removeAll();
        for (int i = 0; i < pinax.length; i++) {

            JPanel panelPiece = new JPanel();
            final JSwitchButton switchButton = new JSwitchButton("on", "off");
            String fullMsg = pinax[i];
            System.out.println(fullMsg);
            String text = null;
            String mode = null;
            if (fullMsg == null) {
                continue;
            }
            if (fullMsg.startsWith("respondGetAllCommandsOutput")) {
                fullMsg = fullMsg.substring("respondGetAllCommandsOutput".length(), fullMsg.length());
            } else if (fullMsg.startsWith("respondGetAllOutput")) {
                fullMsg = fullMsg.substring("respondGetAllOutput".length(), fullMsg.length());
            }
            if (fullMsg.endsWith("on")) {
                text = fullMsg.substring(0, fullMsg.length() - " on".length());
                mode = "on";
                switchButton.setSelected(true);
            } else if (fullMsg.endsWith("off")) {
                text = fullMsg.substring(0, fullMsg.length() - " off".length());
                mode = "off";
                switchButton.setSelected(false);

            } else {
                return;
            }

            final String text2 = text;
            switchButton.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    for (InetAddress inetAddress : AutoConnection.usingInetAddress) {

                        sendData(text2 + switchButton.getText(), inetAddress, AutoConnection.port);

                    }
                }
            });
//            switchButton.addActionListener(new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    System.out.println("Action");
//                    for (InetAddress inetAddress : AutoConnection.usingInetAddress) {
//
//                        sendData(text2 + switchButton.getText(), inetAddress, AutoConnection.port);
//
//                    }
//                }
//
//            });

            panelPiece.add(new JLabel(text));
            panelPiece.add(switchButton);
            centerPanel.add(panelPiece);
        }
        repaint();
        revalidate();
    }
}
