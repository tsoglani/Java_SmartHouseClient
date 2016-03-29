
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tsoglani
 */
public class MenuFrame extends JFrame {

    public static final String voiceButtonText = "Voice command menu.";
    public static final String switchButtonText = "Switch Buttons command menu.";
    public static String usedIP = null;
    public static boolean isAutoSearchMode = true;
    private TextField toggleTextField;

    public MenuFrame() {
        isAutoSearchMode = true;
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JButton voiceCommandButton = new JButton(voiceButtonText);
        JButton switchCommandButton = new JButton(switchButtonText);

        JPanel togglePanel = new JPanel();
        togglePanel.setLayout(new FlowLayout());
        JToggleButton jtb = new JToggleButton("Press Me");
        toggleTextField = new TextField();
        toggleTextField.setColumns(14);
        jtb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
                if (ev.getStateChange() == ItemEvent.SELECTED) {
                    jtb.setText("Auto IP search mode");
                    togglePanel.remove(toggleTextField);
                    isAutoSearchMode = true;
                } else if (ev.getStateChange() == ItemEvent.DESELECTED) {
                    jtb.setText("Manual IP mode");
                    jtb.setBackground(Color.red);
                    togglePanel.add(toggleTextField);
                    isAutoSearchMode = false;
                    revalidate();
                    pack();
                }
            }
        });
        jtb.setSelected(true);
        voiceCommandButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {

                    @Override
                    public void run() {

                        if (isAutoSearchMode) {
                            disableButtonForNSec(5, voiceCommandButton);
                            disableButtonForNSec(5, switchCommandButton);
                            new AutoConnection(voiceButtonText, MenuFrame.this);
                        } else {
                            usedIP = toggleTextField.getText().toString();
                            if (validate(usedIP)) {
                                new AutoConnection(usedIP, voiceButtonText, MenuFrame.this);
                                try {
                                    JOptionPane.showMessageDialog(null, "Sended to "
                                            + "" + InetAddress.getByName(usedIP).getHostAddress() + " .. waiting for respond.");
                                } catch (UnknownHostException ex) {
                                    JOptionPane.showMessageDialog(null, "Please enter a valid IP or DNS of remote device.");
                                }

                            } else {
                                JOptionPane.showMessageDialog(null, "Please enter a valid IP or DNS of remote device.");
                            }
                        }
                    }
                }.start();
            }
        });
        switchCommandButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {

                    @Override
                    public void run() {
                        if (isAutoSearchMode) {
                            disableButtonForNSec(5, voiceCommandButton);
                            disableButtonForNSec(5, switchCommandButton);
                            new AutoConnection(switchButtonText, MenuFrame.this);
                        } else {

                            usedIP = toggleTextField.getText().toString();
                            if (validate(usedIP)) {
                                new AutoConnection(usedIP, switchButtonText, MenuFrame.this);
                                try {
                                    JOptionPane.showMessageDialog(null, "Sended to " + InetAddress.getByName(usedIP).getHostAddress() + " .. waiting for respond.");
                                } catch (UnknownHostException ex) {
                                    JOptionPane.showMessageDialog(null, "Please enter a valid IP or DNS of remote device.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Please enter a valid IP or DNS of remote device.");
                            }
                        }
                    }
                }.start();

            }
        });

        add(voiceCommandButton);
        add(switchCommandButton);
        togglePanel.add(jtb);
//        togglePanel.add(toggleTextField);
        add(togglePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void disableButtonForNSec(final int n, final JButton button) {
        new Thread() {

            @Override
            public void run() {
                try {
                    button.setEnabled(false);
                    Thread.sleep(n * 1000);
                    button.setEnabled(true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Pattern regex = Pattern.compile("[a-z0-9]+[\\.]{1}[a-z0-9]+[\\.]{1}[a-z0-9]+[\\.]{1}[a-z0-9]+");
    private static final Pattern regex2 = Pattern.compile("\\w+\\.\\w+\\.\\w+\\.\\w+");
    private static final String ValidHostnameRegex = "^(?=(?:.*?\\.){2})(?:[a-z][a-z0-9-]*[a-z0-9](?=\\.[a-z]|$)\\.?)+$";

    private static final Pattern reg2 = Pattern.compile(ValidHostnameRegex);

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches() || regex.matcher(ip).matches() || regex2.matcher(ip).matches() || reg2.matcher(ip).matches();
    }
}
