
import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.microphone.MicrophoneAnalyzer;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFileFormat;

import com.darkprograms.speech.recognizer.GoogleResponse;
import com.darkprograms.speech.recognizer.Recognizer;
import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.sourceforge.javaflacencoder.FLACFileWriter;

public class VoiceCommandFrame extends JFrame {
private DatagramSocket clientSocket;
    private MenuFrame menuFrame;

    public VoiceCommandFrame(MenuFrame menuFrame) {
        super("Jarvis Speech API DEMO");
        this.menuFrame = menuFrame;
        try {

            final Microphone mic = new Microphone(FLACFileWriter.FLAC);
            GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

            duplex.setLanguage("en");
//            duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            JTextArea response = new JTextArea();
            response.setEditable(false);
            response.setWrapStyleWord(true);
            response.setLineWrap(true);
            final JButton record = new JButton("Record");
            final JButton stop = new JButton("Stop");
            stop.setEnabled(false);

            record.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {

                        duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
                        record.setEnabled(false);
                        stop.setEnabled(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            stop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    mic.close();
                    record.setEnabled(true);
                    stop.setEnabled(false);
                }
            });
            JLabel infoText = new JLabel("<html><div style=\"text-align: center;\">Just hit record and watch your voice be translated into text.\n<br>Only English is supported by this demo, but the full API supports dozens of languages.<center></html>",
                    0);

            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    menuFrame.setVisible(true);
                    setVisible(false);
                }
            });
            getContentPane().add(backButton);
            getContentPane().add(infoText);
            infoText.setAlignmentX(0.5F);
            JScrollPane scroll = new JScrollPane(response);
            getContentPane().setLayout(new BoxLayout(getContentPane(), 1));
            getContentPane().add(scroll);
            JPanel recordBar = new JPanel();
            getContentPane().add(recordBar);
            recordBar.setLayout(new BoxLayout(recordBar, 0));
            recordBar.add(record);
            recordBar.add(stop);
            setVisible(true);
//            pack();
            setSize(500, 500);
            setLocationRelativeTo(null);

            duplex.addResponseListener(new GSpeechResponseListener() {
                String old_text = "";

                public void onResponse(GoogleResponse gr) {
                    String output = "";
                    output = gr.getResponse();
                    if (gr.getResponse() == null) {
                        this.old_text = response.getText();
                        if (this.old_text.contains("(")) {
                            this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
                        }
                        System.out.println("Paragraph Line Added");
                        
//                        System.out.println("old text"+old_text);
                        mic.close();
                        try {
                            duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (LineUnavailableException ex) {
                            ex.printStackTrace();
                        }
                        

                        for(InetAddress address:AutoConnection.usingInetAddress)
                    sendData(old_text,address,AutoConnection.port);
                        this.old_text = (response.getText() + "\n");
                        this.old_text = this.old_text.replace(")", "").replace("( ", "");
                        response.setText(this.old_text);
                        return;
                    }
                    if (output.contains("(")) {
                        output = output.substring(0, output.indexOf('('));
                    }
                    if (!gr.getOtherPossibleResponses().isEmpty()) {
                        output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
                    }
                    
                    
                    response.setText("");
                    response.append(this.old_text);
                    response.append(output);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(VoiceCommandFrame.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
        private void sendData(final String sendData, final InetAddress IPAddress, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes("UTF-8"), sendData.length(), IPAddress, port);
                    if (clientSocket == null || clientSocket.isClosed())
                        clientSocket = new DatagramSocket();

                    clientSocket.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
        
//         private void receiver() {
//        new Thread() {
//            @Override
//            public void run() {
//               
//                    byte[] receiveData = new byte[1024];
//                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                    try {
//                        if (clientSocket == null || clientSocket.isClosed())
//                            clientSocket = new DatagramSocket();
//                        clientSocket.receive(receivePacket);
//                        String sentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
//                        if(sentence.equalsIgnoreCase("chooseSwitchFunction")){
//                            
//
//                        }else  if(sentence.equalsIgnoreCase("chooseSpeechFunction")){
//                          
//                        }
//
//
//                    } catch (SocketException e) {
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                
//
//            }
//        }.start();
//    }

}



//    static Microphone mic;
//    public static void main(String[] args) {
//        // TODO code application logic here
//        GSpeechDuplex dup = new GSpeechDuplex("AIzaSyB9-y0wdclhKNmi6nSzuCWGSCEFYmyncS0");
//        dup.addResponseListener(new GSpeechResponseListener() {// Adds the listener
//            public void onResponse(GoogleResponse gr) {
//                String output = gr.getResponse();
////                if (output.contains("(")) {
////                    output = output.substring(0, output.indexOf('('));
////                }
////                if (!gr.getOtherPossibleResponses().isEmpty()) {
////                    output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
////                }
//
//                System.out.println("Google thinks you said: " + output);
////                System.out.println("with "
////                        + ((gr.getConfidence() != null) ? (Double.parseDouble(gr.getConfidence()) * 100) : null)
////                        + "% confidence.");
//                System.out.println("Google also thinks that you might have said:"
//                        + gr.getOtherPossibleResponses());
//
//                stringProcess(output);
//            }
//        });
//         mic = new Microphone(FLACFileWriter.FLAC);//Instantiate microphone and have 
//// it record FLAC file.
//        File file = new File("CRAudioTest.flac");//The File to record the buffer to. 
////You can also create your own buffer using the getTargetDataLine() method.
//        while (true) {
//            try {
//                mic.captureAudioToFile(file);//Begins recording
//                Thread.sleep(5000);//Records for 10 seconds
//                //Sends 10 second voice recording to Google
//                byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());//Saves data into memory.
//                dup.recognize(data, (int) mic.getAudioFormat().getSampleRate());
//                mic.getAudioFile().delete();//Deletes Buffer file
//                //REPEAT
//
//            } catch (Exception ex) {
//                ex.printStackTrace();//Prints an error if something goes wrong.
//            } finally {
//                mic.close();//Stops recording
//            }
//        }
//
//    }
//    static boolean readytocommand;
//
//    private static void stringProcess(String input) {
//        Voice voice;
//        VoiceManager voiceManager = VoiceManager.getInstance();
//
//        voice = voiceManager.getVoice("kevin16");
//        voice.allocate();
//        input = input.replace("transcript", "").replace("{", "").replace("}", "").replace("\"", "").replace(":", "");
//        String[] texts = input.split(",");
//
//        //json array
//        boolean found = false;
//        for (String string : texts) {
//            System.out.println(input);
////            string=string.toLowerCase();
//            if (string.contains("command")) {
//                readytocommand = true;
//            } else {
//                readytocommand = false;
//            }
//
//            if (string.equalsIgnoreCase("hi")) {
//                                mic.close();//Stops recording
//                voice.speak("Hi there.");
//                found = true;
//            } else if (string.equals("hello")) {
//                                                mic.close();//Stops recording
//
//                voice.speak("hello sir.");
//                found = true;
//            } else if (string.equalsIgnoreCase("good morning")) {
//                                                mic.close();//Stops recording
//
//                voice.speak("Good morning sir.");
//                found = true;
//            } else if (string.equalsIgnoreCase("how are you")) {
//                voice.speak("I am a robot, I have no feelings sir.");
//                found = true;
//            }
//
//            if (readytocommand) {
//                if (string.equalsIgnoreCase("info") || string.equalsIgnoreCase("information") 
//                        || string.equalsIgnoreCase("informations")) {
//                    voice.speak("I deveped by Nikos Gaitanis.");
//                    found = true;
//                }
//            }
//
//        }
//
//        if (readytocommand && !found && input != null && !input.replaceAll(" ", "").equals("")) {
//            voice.speak("Have no answer");
//        }
//    }
//}
//
////
////   public static void main(String[] args) {
////
////        try {
////            final Microphone mic = new Microphone(FLACFileWriter.FLAC);
////            GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
////            duplex.setLanguage("en");
////            duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
////            duplex.addResponseListener(new GSpeechResponseListener() {
////                String old_text = "";
////
////                public void onResponse(GoogleResponse gr) {
////
////                    output = gr.getResponse();
////                    if (gr.getResponse() == null) {
//////                        this.old_text = response.getText();
//////                        if (this.old_text.contains("(")) {
//////                            this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
//////                        }
//////                        System.out.println("Paragraph Line Added");
//////                        this.old_text = (response.getText() + "\n");
//////                        this.old_text = this.old_text.replace(")", "").replace("( ", "");
//////                        response.setText(this.old_text);
////                        return;
////                    }
//////                                            System.out.println(gr.getResponse());
////
////                    if (output.contains("(")) {
////                        output = output.substring(0, output.indexOf('('));
////                    }
////                    if (!gr.getOtherPossibleResponses().isEmpty()) {
////                        output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
////                    }
////                    System.out.println(output );
////////                    response.setText("");
////////                    response.append(this.old_text);
////////                    response.append(output);
//////                }
//////            }); } catch (IOException ex) {
//////            Logger.getLogger(RecognitionMain.class.getName()).log(Level.SEVERE, null, ex);
//////        } catch (LineUnavailableException ex) {
//////            Logger.getLogger(RecognitionMain.class.getName()).log(Level.SEVERE, null, ex);
//////        }
////                }
////            });
////        } catch (IOException ex) {
////            Logger.getLogger(RecognitionMain.class.getName()).log(Level.SEVERE, null, ex);
////        } catch (LineUnavailableException ex) {
////            Logger.getLogger(RecognitionMain.class.getName()).log(Level.SEVERE, null, ex);
////        }
////    }
////}
