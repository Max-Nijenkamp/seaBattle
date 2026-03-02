package seaBattle.network;

public class ServerMain {

    // main method
    public static void main(String[] args) {
        GameServer server = new GameServer(5000);
        server.start();
    }

}
