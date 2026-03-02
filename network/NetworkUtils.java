package seaBattle.network;

import java.net.*;
import java.io.IOException;
import java.util.Enumeration;

public class NetworkUtils {

    // find an available port
    public static int findAvailablePort(int startPort) {
        for (int port = startPort; port < startPort + 20; port++) {
            try (ServerSocket s = new ServerSocket(port)) {
                return port;
            } catch (IOException ignored) {
            }
        }
        return -1;
    }

    // get the local IP address
    public static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown IP";
    }
}
