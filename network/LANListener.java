package seaBattle.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LANListener {

    public interface ServerFoundCallback {
        void onServerFound(String ip, String name, int port);
    }

    // listen for servers
    public void listen(ServerFoundCallback callback) {

        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(8888);

                byte[] buffer = new byte[256];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());

                    if (msg.startsWith("SEABATTLE_HOST")) {
                        String[] parts = msg.split(";");
                        String name = parts[1];
                        int port = Integer.parseInt(parts[2]);

                        String ip = packet.getAddress().getHostAddress();
                        callback.onServerFound(ip, name, port);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
