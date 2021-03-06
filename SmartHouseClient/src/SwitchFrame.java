
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

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
        receiver();
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
                    isOnCommandMode=false;
                } else if (modeToggle.getText().equals(outputsMode)) {
                    modeToggle.setText(commandsMode);
                isOnCommandMode=true;
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
                menuFrame.repaint();
                menuFrame.revalidate();
                isRunning = false;
                if (clientSocket != null) {
                    try {
                        clientSocket.disconnect();
                        clientSocket.close();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                }
                clientSocket = null;
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
        // receiver();
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("send data : " + sendData);
                    DatagramPacket sendPacket = new DatagramPacket((Main.UNIQUE_USER_ID + sendData).getBytes("UTF-8"), (Main.UNIQUE_USER_ID + sendData).length(), IPAddress, port);
                    if (clientSocket == null) {
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
    private boolean isRunning = true;

    private void receiver() {
        new Thread() {
            @Override
            public void run() {

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    while (isRunning) {
                        if (clientSocket == null) {
                            clientSocket = new DatagramSocket();
                        }
                        // clientSocket.setSoTimeout(3000);
                        clientSocket.receive(receivePacket);
                        String sentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                        System.out.println(sentence);
                        processString(sentence);
                    }
                } catch (java.net.SocketTimeoutException e) {
                    System.out.println("close receiver thread");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void processString(String input) {
        System.out.println("input : " + input);
        if (input.contains("@@@")) {

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
                    if (isOnCommandMode) {
                        centerPanel.removeAll();
                        fullMsg = fullMsg.substring("respondGetAllCommandsOutput".length(), fullMsg.length());
                    } else {
                        return;
                    }
                } else if (fullMsg.startsWith("respondGetAllOutput")) {
                    if (!isOnCommandMode) {
                        centerPanel.removeAll();
                        fullMsg = fullMsg.substring("respondGetAllOutput".length(), fullMsg.length());
                    } else {
                        return;
                    }
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

                switchButton.addMouseListener(new MouseInputListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        for (InetAddress inetAddress : AutoConnection.usingInetAddress) {
                            String power = null;
                            if (switchButton.getText().equals("on")) {
                                power = "off";
                            } else if (switchButton.getText().equals("off")) {
                                power = "on";
                            }

                            if (power != null) {
                                sendData("switch " + text2 + " " + power, inetAddress, AutoConnection.port);
                            }

                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                    }
                });
//            switchButton.addChangeListener(new ChangeListener() {

//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    for (InetAddress inetAddress : AutoConnection.usingInetAddress) {
//                        String power=null;
//                        if (switchButton.getText().equals("on")) {
//                            power="off";
//                        } else if (switchButton.getText().equals("off")) {
//                             power="on";
//                        }
//                        
//                        if(power!=null)
//                        sendData(text2 + " "+power, inetAddress, AutoConnection.port);
//                        
//                    }
//                }
//            });
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
        } else {

            for (int i = 0; i < centerPanel.getComponentCount(); i++) {
                Component component = centerPanel.getComponent(i);
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    JSwitchButton sb = (JSwitchButton) panel.getComponent(1);

//                    for (int j = 0; j < panel.getComponentCount(); j++) {
                    JLabel label = (JLabel) panel.getComponent(0);

//                        if (panelPiece instanceof JSwitchButton) {
//                            System.out.println("eeeeee");
//
//                            sb = (JSwitchButton) panelPiece;
                    if (input.startsWith("switch")) {
                        input = input.substring("switch ".length(), input.length());
//                            }

                    }
//                        if (panelPiece instanceof JLabel) {
//                            JLabel label = (JLabel) panelPiece;
                    if (input.startsWith(label.getText())) {
                        String power = input.substring(label.getText().length(), input.length());
                        if (power.startsWith(" ")) {
                            power = power.substring(" ".length(), power.length());
                        }
                        if (power.equals("on")) {
                            sb.setSelected(true);
                        } else if (power.equals("off")) {
                            sb.setSelected(false);
                        }
                    }

//                        }
//                    }
                }
            }
        }
    }
}
