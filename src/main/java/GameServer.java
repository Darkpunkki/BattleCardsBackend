import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class GameServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private CountDownLatch latch;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);


        latch = new CountDownLatch(2);

        // Accept client connections
        while (clients.size() < 2) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, latch);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
            System.out.println("Client connected: " + clientSocket.getInetAddress());
        }

        try {
            latch.await(); // Wait until latch counts down to zero
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start the game after two clients have connected
        startGame();
    }

    private void startGame() {
        TradingCardGame game = new TradingCardGame(clients);
        System.out.println("Starting game with " + clients.size() + " players.");
        game.play(clients.size());
    }

}
