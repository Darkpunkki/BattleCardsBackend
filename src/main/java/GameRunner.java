import java.io.IOException;

public class GameRunner {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("PORT"));
        GameServer server = new GameServer();
        try {
            server.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
