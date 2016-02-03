
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

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

public static final String voiceButtonText="Voice command menu.";
public static final String switchButtonText="Switch Buttons command menu.";

    public MenuFrame() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JButton voiceCommandButton = new JButton(voiceButtonText);
        JButton switchCommandButton = new JButton(switchButtonText);
        voiceCommandButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
           new AutoConnection(voiceButtonText, MenuFrame.this);
            }
        });
        switchCommandButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
           new AutoConnection(switchButtonText, MenuFrame.this);
            }
        });
        
        add(voiceCommandButton);
        add(switchCommandButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    
}
