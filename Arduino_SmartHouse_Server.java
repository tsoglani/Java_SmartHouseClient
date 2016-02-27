/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino_smarthouse_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sintef.jarduino.DigitalPin;
import org.sintef.jarduino.DigitalState;
import org.sintef.jarduino.JArduino;
import org.sintef.jarduino.PinMode;
import org.sintef.jarduino.comm.Serial4JArduino;
//import org.sintef.jarduino.comm.Serial4JArduino;
/*
 Blink
 Turns on an LED on for one second, then off for one second, repeatedly.
 This example code is in the public domain.
 */

public class Arduino_SmartHouse_Server extends JArduino {

    private DatagramSocket serverSocket;
    //// user editable part
    // Pay attention on **
    private static final int NumberOfBindingCommands = 4;// relay number of input chanels. Change it if is different than four.

    private final static int port = 2222; // default port can change it, but you have to change it also in android device,
    //not recomented to change it

    ///** every startingDeviceID must be unique in every raspberry device contected in local network.
    private final static int DeviceID = 0; // Example: if we have 4 raspberry devices connected in local network, each one MUST have a unique ID :
    // the first Ruspberry device DeviceID will be 0, the second device's DeviceID will be 1
    // the third will be 2 the fourth will be 3 ...    (it is very important)
    private ArrayList<String>[] outputPowerCommands = new ArrayList[NumberOfBindingCommands];
    private ArrayList<Integer>[] activatePortOnCommand = new ArrayList[NumberOfBindingCommands];
    private ArrayList<String>[] outputCommands = new ArrayList[20]; // list of outputs
    private ArrayList<String> ON, OFF;// = "on", OFF = "off";// word you have to use at the end of the command to activate or deactivate
    private ArrayList<String> ONAtTheStartOfSentence, OFFAtTheStartOfSentence;

    public Arduino_SmartHouse_Server(String port) {
        super(port);
    }
    boolean isOnSwitchView;

    @Override
    protected void setup() {
        try {
            initArrays();
            initStates();
            initializeOutputCommands();
            initializePowerCommands();
            pinMode(DigitalPin.PIN_0, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_1, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_2, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_3, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_4, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_5, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_6, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_7, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_8, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_9, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_10, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_11, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_12, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_13, PinMode.OUTPUT);

            pinMode(DigitalPin.A_0, PinMode.OUTPUT);
            pinMode(DigitalPin.A_1, PinMode.OUTPUT);
            pinMode(DigitalPin.A_2, PinMode.OUTPUT);
            pinMode(DigitalPin.A_3, PinMode.OUTPUT);
            pinMode(DigitalPin.A_4, PinMode.OUTPUT);
            pinMode(DigitalPin.A_5, PinMode.OUTPUT);
            isOnSwitchView = false;
            serverSocket = new DatagramSocket(port);
            System.out.println("Waiting for data..");
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void loop() {
        try {
            //        // set the LED on
//        digitalWrite(DigitalPin.PIN_12, DigitalState.HIGH);
//        delay(1000); // wait for a second
//        // set the LED off
//        digitalWrite(DigitalPin.PIN_12, DigitalState.LOW);
//        delay(1000); // wait for a second
            startReceivingData();
        } catch (IOException ex) {
            Logger.getLogger(Arduino_SmartHouse_Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        String serialPort ;

        if (args.length == 1) {
            serialPort = args[0];
        } else {
          
            serialPort = Serial4JArduino.selectSerialPort();
        }
        JArduino arduino = new Arduino_SmartHouse_Server(serialPort);
        arduino.runArduinoProcess();
    }

    ///** 
    // these are the commannds that each device can receive and react,
    // so every outputPowerCommand must be unique in every device contected in local network.
    private void initializePowerCommands() {

        for (int i = 0; i < NumberOfBindingCommands; i++) {
            switch (i) {
                case 0:
                    //Number of command you can put in one Device:outputPowerCommands[0]... outputPowerCommands[RelayNumberOfChanels-1] NO MORE THAN 'RelayNumberOfChanels-1'
                    // else you will have an error mesasge
                    //ALL commands WITH LATIN LETERS 
                    addCommandsAndPorts(i // number of command
                            , new String[]{"kitchen lights", "kitchen light", "koyzina fos", "koyzina fota", "koyzinas fos", "koyzinas fota", "fos koyzina", "fota koyzina", "fos koyzinas", "fota koyzinas"},// command text for reaction
                            new Integer[]{2, 4, 7} // on command 0 these outputs will open or close at once when the previous commands received
                    );
                    break;

                case 1:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"room light", "room lights", "bedroom light", "bedroom lights", "domatio fos",// command text for reaction
                                "domatio fota", "fos domatio", "fota domatio"},
                            new Integer[]{3, 5, 1});// on command 1 these outputs will open or close at once when the previous commands received
                    break;

                case 2:
                    //Number of command you can put in one Device:outputPowerCommands[0]... outputPowerCommands[RelayNumberOfChanels-1] NO MORE THAN 'RelayNumberOfChanels-1'
                    // else you will have an error mesasge
                    //ALL commands WITH LATIN LETERS 
                    addCommandsAndPorts(i // number of command
                            , new String[]{"office lights", "office light",},// command text for reaction
                            new Integer[]{8, 9, 10} // on command 0 these outputs will open or close at once when the previous commands received
                    );
                    break;

                case 3:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"tv", "television"},
                            new Integer[]{11, 12, 13});// on command 1 these outputs will open or close at once when the previous commands received
                    break;
                case 4:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"kitchen"},
                            new Integer[]{14, 15});// on command 1 these outputs will open or close at once when the previous commands received
                case 5:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"air condition", "cooler"},
                            new Integer[]{16});// on command 1 these outputs will open or close at once when the previous commands received
                case 6:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"garage"},
                            new Integer[]{17, 18, 19});// on command 1 these outputs will open or close at once when the previous commands received
                case 7:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"toilet light", "toilet lights"},
                            new Integer[]{20, 21});// on command 1 these outputs will open or close at once when the previous commands received

            }
        }

    }

    //// end of user editable part
    private ArrayList<InetAddress> addresses = new ArrayList<InetAddress>() {

        @Override
        public boolean add(InetAddress e) {
            if (!contains(e)) {
                return super.add(e);
            }
            return false;
        }
    };

    private void initArrays() {

        for (int i = 0; i < NumberOfBindingCommands; i++) {
            outputPowerCommands[i] = new ArrayList<String>();

            activatePortOnCommand[i] = new ArrayList<Integer>();
        }
    }

    // add commands text for reaction and the ports that want to react 
    private void addCommandsAndPorts(int number, String[] reactOnCommands, Integer[] ports) {
        for (int i = 0; i < reactOnCommands.length; i++) {
            outputPowerCommands[number].add(reactOnCommands[i]);
        }
        for (int i = 0; i < ports.length; i++) {
            activatePortOnCommand[number].add(ports[i]);
        }
    }

    private void addCommands(int number, String... reactOnCommands) {
        for (int i = 0; i < reactOnCommands.length; i++) {
            outputPowerCommands[number].add(reactOnCommands[i]);
        }
    }

    private void addPortsOnCommand(int number, Integer... ports) {
        for (int i = 0; i < ports.length; i++) {
            activatePortOnCommand[number].add(ports[i]);
        }
    }

    // greek letters match ( must be latin characters )
    //α=a,β=v,γ=g,δ=d,ε=e,ζ=z,  η=i,ι=i,θ=th,κ=k,
    //λ=l,μ =m, ν=n, ξ=ks, o=ο,ω=o,π=p,ρ=r,
    //ς=s,σ=s,τ=t,υ=y,φ=f,χ=x,ψ=ps
    //in this function you add multi command for each output.
    // these EXACT commands you must send from the Android device (speech or with Switch buttons ) to activate or deactivate the device output
    // Example send command "kitchen light" and "on" or "off" to activate or deactivate the device in output 0. 
    //You can modify your commands.
    private void initializeOutputCommands() {

        for (int i = 0; i < outputCommands.length; i++) {
            outputCommands[i] = new ArrayList<String>();
            String extraOnStart = null;
            if (i >= 13) {
                extraOnStart = "pin ";
            } else {
                extraOnStart = "A ";
            }
            outputCommands[i].add(extraOnStart + DeviceID + " output " + (i));
        }

    }

    private void initStates() {
        ON = new ArrayList<String>();
        OFF = new ArrayList<String>();
        ONAtTheStartOfSentence = new ArrayList<String>();
        OFFAtTheStartOfSentence = new ArrayList<String>();
        ON.add("on");
        ON.add("start");
        ON.add("open");
        OFF.add("off");
        OFF.add("stop");
        OFF.add("close");

        ONAtTheStartOfSentence.add("open");
        ONAtTheStartOfSentence.add("anoikse");
        OFFAtTheStartOfSentence.add("close");
        OFFAtTheStartOfSentence.add("kleise");

    }

    private void startReceivingData() throws IOException {

        isOnSwitchView = false;
        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        String sentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        System.out.println(sentence);
        if (sentence.startsWith("switch ")) {
            isOnSwitchView = true;
            sentence = sentence.substring("switch ".length(), sentence.length());
            sendData(sentence, receivePacket.getAddress(), receivePacket.getPort());
        }

//            if (isOnSwitchView) { // on switch mode return data to say that the info is here and the light will toght so the toggle button to change status
//                // this data sends here only on Auto switch view mode
//                sendData(sentence, receivePacket.getAddress(), receivePacket.getPort());
////                System.out.println("send " + sentence);
//            }
        if (sentence.equalsIgnoreCase("chooseSpeechFunction") || sentence.equalsIgnoreCase("chooseSwitchFunction")) {// used when connect for first time and send ok back, when the android receive the ok open to next view
            sendData(sentence, receivePacket.getAddress(), receivePacket.getPort());

        }

        if (sentence.startsWith("returning")) {// used when connect for first time and send ok back, when the android receive the ok open to next view
            sendData("ok", receivePacket.getAddress(), receivePacket.getPort());
            System.out.println("<ok/>");
            addresses.add(receivePacket.getAddress());
        }

//            if (sentence.startsWith("manual_switch_view")) {
//                String msg = "works";// getAllOutput();
//                sendData(msg, receivePacket.getAddress(), receivePacket.getPort());
//                System.out.println("send data");
//            }
        if (sentence.startsWith("getAllOutput")) { // I say than I need all the outputs
            String msg = getAllOutput();//"getput on@@@getoutt2 off";//
            if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
                sendData("respondGetAllOutput" + msg, receivePacket.getAddress(), receivePacket.getPort());
            }
        }
        if (sentence.startsWith("getAllCommandsOutput")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )

            String msg = getAllCommandOutput();//"getput on@@@getoutt2 off";//
            if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
                sendData("respondGetAllCommandsOutput" + msg, receivePacket.getAddress(), receivePacket.getPort());
            }
            System.out.println(msg);
        }

        if (sentence.startsWith("update_manual_mode")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )

            String msg = "kouzina fwta on@@@domatio fos on";//getAllOutput();
            if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
                sendData("update_manual_mode" + msg, receivePacket.getAddress(), receivePacket.getPort());
            }

        }
        boolean existAsLed = processLedString(sentence);
        if (!existAsLed) {
            processCommandString(sentence);
        }

    }

    private String getAllOutput() {

        String output = new String();
        try {
            DigitalState ds = null;
            String isDoing = new String();
            for (int i = 0; i < outputCommands.length; i++) {

                switch (i) {
                    case 0:
                        ds = digitalRead(DigitalPin.PIN_0);
                        break;
                    case 1:
                        ds = digitalRead(DigitalPin.PIN_1);
                        break;
                    case 2:
                        ds = digitalRead(DigitalPin.PIN_2);
                        break;
                    case 3:
                        ds = digitalRead(DigitalPin.PIN_3);
                        break;
                    case 4:
                        ds = digitalRead(DigitalPin.PIN_4);
                        break;
                    case 5:
                        ds = digitalRead(DigitalPin.PIN_5);
                        break;
                    case 6:
                        ds = digitalRead(DigitalPin.PIN_6);
                        break;
                    case 7:
                        ds = digitalRead(DigitalPin.PIN_7);
                        break;
                    case 8:
                        ds = digitalRead(DigitalPin.PIN_8);
                        break;
                    case 9:
                        ds = digitalRead(DigitalPin.PIN_9);
                        break;
                    case 10:
                        ds = digitalRead(DigitalPin.PIN_10);
                        break;
                    case 11:
                        ds = digitalRead(DigitalPin.PIN_11);
                        break;
                    case 12:
                        ds = digitalRead(DigitalPin.PIN_12);
                        break;
                    case 13:
                        ds = digitalRead(DigitalPin.PIN_13);
                        break;
//                  

                    case 14:
                        ds = digitalRead(DigitalPin.A_0);
                        break;
                    case 15:
                        ds = digitalRead(DigitalPin.A_1);
                        break;
                    case 16:
                        ds = digitalRead(DigitalPin.A_2);
                        break;
                    case 17:
                        ds = digitalRead(DigitalPin.A_3);
                        break;
                    case 18:
                        ds = digitalRead(DigitalPin.A_4);
                        break;
                    case 19:
                        ds = digitalRead(DigitalPin.A_5);
                        break;
//                    case 20:
//                     ds = digitalRead(DigitalPin.A_5);
//                        break;

                }
                if (ds == null) {
                    continue;
                }
                if (ds.equals(DigitalState.HIGH)) {
                    isDoing = ON.get(0);
                } else if (ds.equals(DigitalState.LOW)) {
                    isDoing = OFF.get(0);
                }

                //for (int j = 0; j < outputCommands[i].size(); j++) {
                if (i != 0) {
                    output += "@@@";

                }
                output += outputCommands[i].get(0) + " " + isDoing;

                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    private String getAllCommandOutput() {

        String output = new String();
        try {
            DigitalState ds = null;

            for (int i = 0; i < outputPowerCommands.length; i++) {
                ArrayList<String> isOpenList = new ArrayList<String>();
                String finalIsDoing = ON.get(0);
                for (int j = 0; j < activatePortOnCommand[i].size(); j++) {

                    String isDoing = new String();
                    switch (activatePortOnCommand[i].get(j)) {
                        case 0:
                            ds = digitalRead(DigitalPin.PIN_0);
                            break;
                        case 1:
                            ds = digitalRead(DigitalPin.PIN_1);
                            break;
                        case 2:
                            ds = digitalRead(DigitalPin.PIN_2);
                            break;
                        case 3:
                            ds = digitalRead(DigitalPin.PIN_3);
                            break;
                        case 4:
                            ds = digitalRead(DigitalPin.PIN_4);
                            break;
                        case 5:
                            ds = digitalRead(DigitalPin.PIN_5);
                            break;
                        case 6:
                            ds = digitalRead(DigitalPin.PIN_6);
                            break;
                        case 7:
                            ds = digitalRead(DigitalPin.PIN_7);
                            break;
                        case 8:
                            ds = digitalRead(DigitalPin.PIN_8);
                            break;
                        case 9:
                            ds = digitalRead(DigitalPin.PIN_9);
                            break;
                        case 10:
                            ds = digitalRead(DigitalPin.PIN_10);
                            break;
                        case 11:
                            ds = digitalRead(DigitalPin.PIN_11);
                            break;
                        case 12:
                            ds = digitalRead(DigitalPin.PIN_12);
                            break;
                        case 13:
                            ds = digitalRead(DigitalPin.PIN_13);
                            break;
//                      

                        case 14:
                            ds = digitalRead(DigitalPin.A_0);
                            break;
                        case 15:
                            ds = digitalRead(DigitalPin.A_1);
                            break;
                        case 16:
                            ds = digitalRead(DigitalPin.A_2);
                            break;
                        case 17:
                            ds = digitalRead(DigitalPin.A_3);
                            break;
                        case 18:
                            ds = digitalRead(DigitalPin.A_4);
                            break;
                        case 19:
                            ds = digitalRead(DigitalPin.A_5);
                            break;
//                        case 20:
//                            ds = digitalRead(DigitalPin.A_5);
//                            break;

                    }
                    if (ds == null) {
                        continue;
                    }
                    if (ds.equals(DigitalState.HIGH)) {
                        isDoing = ON.get(0);
                    } else if (ds.equals(DigitalState.LOW)) {
                        isDoing = OFF.get(0);
                    }
                    isOpenList.add(isDoing);

                }
                if (i != 0) {
                    output += "@@@";

                }
                if (isOpenList.contains("off")) {
                    finalIsDoing = OFF.get(0);
                }
                if (!outputPowerCommands[i].isEmpty()) {
                    output += outputPowerCommands[i].get(0) + " " + finalIsDoing;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    private void sendData(String msg, InetAddress IPAddress, int port) throws IOException {
        byte[] sendData;
        sendData = msg.getBytes();
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
    }

    private void processCommandString(String input) {
        String isDoing = "off";

        for (int i = 0; i < outputPowerCommands.length; i++) {

            for (int j = 0; j < outputPowerCommands[i].size(); j++) {

                if (input.startsWith(outputPowerCommands[i].get(j))) {
                    isDoing = input.replace(outputPowerCommands[i].get(j), "").replaceAll(" ", "");
                } else {

                    String firstWord = input.split(" ")[0];
                    if (OFFAtTheStartOfSentence.contains(firstWord)) {
                        isDoing = "off";
                        input = input.substring((firstWord + " ").length() + 1, input.length());
                    } else if (ONAtTheStartOfSentence.contains(firstWord)) {
                        isDoing = "on";
                        input = input.substring((firstWord + " ").length() + 1, input.length());

                    }
                }
                if (input.startsWith(outputPowerCommands[i].get(j))) {

                    if (ON.contains(isDoing)) {
                        System.out.println("found command " + outputPowerCommands[i].get(j) + " on" + ", these ports will open: " + activatePortOnCommand[i]);
                        for (int k = 0; k < activatePortOnCommand[i].size(); k++) {
                            ToggleLedNo(activatePortOnCommand[i].get(k), ON.get(0));
                        }
                    } else if (OFF.contains(isDoing)) {
                        System.out.println("found command " + outputPowerCommands[i].get(j) + " off" + ", these ports will close: " + activatePortOnCommand[i]);
                        for (int k = 0; k < activatePortOnCommand[i].size(); k++) {
                            ToggleLedNo(activatePortOnCommand[i].get(k), OFF.get(0));
                        }
                    }
                }

            }
        }
    }

    private void ToggleLedNo(int number, String state) {
        DigitalPin dpin = null;
        switch (number) {
            case 0:
                dpin = DigitalPin.PIN_0;
                break;
            case 1:
                dpin = DigitalPin.PIN_1;
                break;
            case 2:
                dpin = DigitalPin.PIN_2;
                break;
            case 3:
                dpin = DigitalPin.PIN_3;
                break;
            case 4:
                dpin = DigitalPin.PIN_4;
                break;
            case 5:
                dpin = DigitalPin.PIN_5;
                break;
            case 6:
                dpin = DigitalPin.PIN_6;
                break;
            case 7:
                dpin = DigitalPin.PIN_7;
                break;
            case 8:
                dpin = DigitalPin.PIN_8;
                break;
            case 9:
                dpin = DigitalPin.PIN_9;
                break;
            case 10:
                dpin = DigitalPin.PIN_10;
                break;
            case 11:
                dpin = DigitalPin.PIN_11;
                break;
            case 12:
                dpin = DigitalPin.PIN_12;
                break;
            case 13:
                dpin = DigitalPin.PIN_13;
                break;
//           

            case 14:
                dpin = DigitalPin.A_0;
                break;
            case 15:
                dpin = DigitalPin.A_1;
                break;
            case 16:
                dpin = DigitalPin.A_2;
                break;
            case 17:
                dpin = DigitalPin.A_3;
                break;
            case 18:
                dpin = DigitalPin.A_4;
                break;
            case 19:
                dpin = DigitalPin.A_5;
                break;
//            case 20:
//               dpin = DigitalPin.A_5;
//                break;

        }
        if (dpin == null) {
            return;
        }
        if (state.equalsIgnoreCase(ON.get(0))) {
            digitalWrite(dpin, DigitalState.HIGH);
        } else if (state.equalsIgnoreCase(OFF.get(0))) {
            digitalWrite(dpin, DigitalState.LOW);
        }
    }

    private boolean processLedString(String input) {
        // get a handle to the GPIO controller
        boolean found = false;
        String isDoing = "off"; // creating the pin with parameter PinState.HIGH
        // will instantly power up the pin
        for (int i = 0; i < outputCommands.length; i++) {

            for (int j = 0; j < outputCommands[i].size(); j++) {
                if (input.startsWith(outputCommands[i].get(j))) {
                    isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                } else {
                    try {
                        String firstWord = input.split(" ")[0];
                        if (OFFAtTheStartOfSentence.contains(firstWord)) {
                            isDoing = "off";
                            input = input.substring((firstWord + " ").length() + 1, input.length());
                        } else if (ONAtTheStartOfSentence.contains(firstWord)) {
                            isDoing = "on";
                            input = input.substring((firstWord + " ").length() + 1, input.length());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (input.startsWith(outputCommands[i].get(j))) {
                    found = true;
                    switch (i) {
                        case 0:// output no 0
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_0, DigitalState.HIGH);
                                System.out.println("led 0 on");
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_0, DigitalState.LOW);
                                System.out.println("led 0 off");
                            }

                            break;
                        case 1:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                System.out.println("led 1 on");
                                digitalWrite(DigitalPin.PIN_1, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                System.out.println("led 1 off");
                                digitalWrite(DigitalPin.PIN_1, DigitalState.LOW);
                            }
                            break;
                        case 2:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                System.out.println("led 2 on");
                                digitalWrite(DigitalPin.PIN_2, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_2, DigitalState.LOW);
                                System.out.println("led 2 off");
                            }
                            break;
                        case 3:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_3, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_3, DigitalState.LOW);
                            }
                            break;
                        case 4:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_4, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_4, DigitalState.LOW);
                            }

                            break;
                        case 5:// output no 1

                            if (ON.contains(isDoing)) {
                                System.out.println("led 5 on");
                                digitalWrite(DigitalPin.PIN_5, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                System.out.println("led 5 off");
                                digitalWrite(DigitalPin.PIN_5, DigitalState.LOW);
                            }

                            break;
                        case 6:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_6, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_6, DigitalState.LOW);
                            }

                            break;
                        case 7:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_7, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_7, DigitalState.LOW);
                            }

                            break;
                        case 8:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_8, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_8, DigitalState.LOW);
                            }

                            break;
                        case 9:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_9, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_9, DigitalState.LOW);
                            }

                            break;
                        case 10:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_10, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_10, DigitalState.LOW);
                            }

                            break;
                        case 11:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_11, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_11, DigitalState.LOW);
                            }

                            break;

                        case 12:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_12, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_12, DigitalState.LOW);
                            }

                            break;
                        case 13:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_13, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_13, DigitalState.LOW);
                            }

                            break;

                        case 14:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_0, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_0, DigitalState.LOW);
                            }

                            break;
                        case 15:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_1, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_1, DigitalState.LOW);
                            }

                            break;
                        case 16:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_2, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_2, DigitalState.LOW);
                            }

                            break;
                        case 17:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_3, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_3, DigitalState.LOW);
                            }

                            break;
                        case 18:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_4, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_4, DigitalState.LOW);
                            }

                            break;
                        case 19:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_5, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_5, DigitalState.LOW);
                            }

                            break;
//                        case 20:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
//                            if (ON.contains(isDoing)) {
//                                digitalWrite(DigitalPin.A_5, DigitalState.HIGH);
//                            } else if (OFF.contains(isDoing)) {
//                              digitalWrite(DigitalPin.A_5, DigitalState.LOW);
//                            }

//                            break;
                    }

                }

            }
        }

//        pin.high();
//        System.out.println("light is: ON");
//
//        // wait 2 seconds
//        Thread.sleep(2000);
//
//        // turn off GPIO 1
//        pin.low();
//        System.out.println("light is: OFF");
//
//        // wait 1 second
//        Thread.sleep(1000);
//
//        // turn on GPIO 1 for 1 second and then off
//        System.out.println("light is: ON for 1 second");
//        pin.pulse(1000, true);
//
//        // release the GPIO controller resources
        return found;
    }

}
