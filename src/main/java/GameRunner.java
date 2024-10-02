import java.io.IOException;

public class GameRunner {
    public static void main(String[] args) {
        int port = 12345;
        GameServer server = new GameServer();
        try {
            server.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
