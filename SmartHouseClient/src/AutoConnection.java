
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tsoglani on 18/1/2016.
 */
public class AutoConnection {

    static int port = 2222;
    private DatagramSocket clientSocket;
    private String input;
    private MenuFrame menuFrame;
    static ArrayList<InetAddress> usingInetAddress = new ArrayList<InetAddress>() {
        @Override
        public boolean add(InetAddress object) {
            if (!contains(object)) {
                return super.add(object);
            }
            return false;
        }
    };

    public AutoConnection(String input, MenuFrame menuFrame) {
        this.input = input;
        this.menuFrame = menuFrame;
        try {
            sendToAllIpInNetwork();
        } catch (IOException ex) {
            Logger.getLogger(AutoConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AutoConnection(String ip, String input, MenuFrame menuFrame) {
        this.input = input;
        this.menuFrame = menuFrame;
        try {
            sendToSpesificIpInNetwork(ip);
        } catch (Exception ex) {
            Logger.getLogger(AutoConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendToSpesificIpInNetwork(String ip) throws SocketException, UnknownHostException, IOException {
        if (clientSocket == null || !clientSocket.isConnected()) {
            clientSocket = new DatagramSocket();
        }
        receiver();

        // Log.e("ip=",checkIp);
        String sendData = (Main.UNIQUE_USER_ID) + "returning";
        InetAddress IPAddress = InetAddress.getByName(ip);
        DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), IPAddress, port);
        clientSocket.send(sendPacket);

    }

    public void sendToAllIpInNetwork() throws UnknownHostException, IOException {
        ArrayList<String> ipList = getLocal();
        if (clientSocket == null || !clientSocket.isConnected()) {
            clientSocket = new DatagramSocket();
        }
        receiver();
        for (final String ip : ipList) {
            if (ip.replaceAll(" ", "").equals("")) {
                continue;
            }
            try {
                for (int i = 1; i < 255; i++) {
                    final String checkIp = ip + i;
                    // Log.e("ip=",checkIp);
                    String sendData = (Main.UNIQUE_USER_ID) + "returning";
                    InetAddress IPAddress = InetAddress.getByName(checkIp);
                    DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), IPAddress, port);
                    clientSocket.send(sendPacket);

                }
            } catch (Exception e) {
                e.printStackTrace();
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
                    while(true){
                    clientSocket.setSoTimeout(4000);
                    clientSocket.receive(receivePacket);
                    String modifiedSentence = new String(receivePacket.getData()); // 1rst connection respond
                    System.out.println(modifiedSentence);
                    if (!usingInetAddress.contains(receivePacket.getAddress())) {
                        usingInetAddress.add(receivePacket.getAddress());
                    }
                    ///
                    if (input.equals(MenuFrame.voiceButtonText)) {
 closeClientSoccet();
                        VoiceCommandFrame voiceCommandFrame = new VoiceCommandFrame(menuFrame);
                        menuFrame.setVisible(false);
                       

                    } else if (input.equals(MenuFrame.switchButtonText)) {
                         closeClientSoccet();
                        SwitchFrame switchFrame = new SwitchFrame(menuFrame);
                        menuFrame.setVisible(false);
                       
                    }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void closeClientSoccet() {
        try {
            clientSocket.disconnect();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientSocket = null;
    }

    private static ArrayList<String> getLocal() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        ArrayList<String> list = new ArrayList<String>();
        while (e.hasMoreElements()) {

            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {

                InetAddress inet = (InetAddress) ee.nextElement();
                if (!inet.isLinkLocalAddress()) {
                    String hostAdd = inet.getHostAddress();
                    String str = "";
                    String[] ars = hostAdd.split("\\.");
                    for (int j = 0; j < ars.length - 1; j++) {
                        str += ars[j] + ".";
                    }
                    list.add(str);
                }
            }
        }
        return list;
    }

}
