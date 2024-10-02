import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Player player;
    private CountDownLatch latch;

    public ClientHandler(Socket socket, CountDownLatch latch) {
        this.socket = socket;
        this.latch = latch;
    }

    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            // Prompt for player name
            sendMessage("Welcome to the game! Please enter your name:");
            String name = readMessage().toString();
            player = new Player(name, new IncreasingEnergyStrategy(10, 1));

            // Wait until the game starts...
            latch.countDown();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Send a message to the client
    public synchronized void sendMessage(Object message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    // Read a message from the client
    public synchronized Object readMessage() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
