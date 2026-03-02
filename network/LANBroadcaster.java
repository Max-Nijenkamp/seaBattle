package seaBattle.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LANBroadcaster {

    private boolean running = true;

    // start the broadcast
    public void startBroadcast(String username, int port) {

        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                while (running) {

                    String message = "SEABATTLE_HOST;" + username + ";" + port;
                    byte[] data = message.getBytes();

                    DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            InetAddress.getByName("255.255.255.255"),
                            8888
                    );

                    socket.send(packet);

                    Thread.sleep(1000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
