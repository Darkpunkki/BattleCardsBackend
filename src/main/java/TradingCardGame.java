import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TradingCardGame extends Game {

    private List<Player> players;
    private int turnNumber = 1;
    private List<ClientHandler> clientHandlers;

    public TradingCardGame(List<ClientHandler> clientHandlers) {
        this.clientHandlers = clientHandlers;
    }

    @Override
    protected void initializeGame(int numberOfPlayers) {
        // Initialize players and their decks
        players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = clientHandlers.get(i).getPlayer();
            players.add(player);
        }
    }

    @Override
    protected boolean endOfGame() {
        // Game ends when any player's health is <= 0
        for (Player player : players) {
            if (player.getHealth() <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void playSingleTurn(int playerIndex) {
        Player currentPlayer = players.get(playerIndex);
        Player opponent = GameUtils.getOpponent(currentPlayer, players);
        ClientHandler currentClient = clientHandlers.get(playerIndex);
        ClientHandler opponentClient = clientHandlers.get((playerIndex + 1) % players.size());

        try {
            // Notify current player it's their turn
            currentClient.sendMessage("It's your turn, " + currentPlayer.getName() + "!");

            // Replenish energy at the start of the turn
            String replenishMessage = currentPlayer.replenishEnergy(turnNumber);
            currentClient.sendMessage(replenishMessage);

            // Draw cards at the start of the turn
            List<Card> initialDrawnCards = currentPlayer.drawCards(1);
            currentClient.sendMessage("You drew: " + initialDrawnCards.get(0).toString());

            // Send player status and hand
            currentClient.sendMessage(currentPlayer.getStatusWithBars());
            currentClient.sendMessage(currentPlayer.getHandWithVisuals());

            boolean turnEnded = false;

            while (!turnEnded) {
                StringBuilder actionPrompt = new StringBuilder();
                actionPrompt.append("Your current status:\n").append(currentPlayer.getStatusWithBars()).append("\n");
                actionPrompt.append("Actions:\n1. Play a card\n2. Draw an additional card (3 energy)\n3. End turn\n4. View card info\n");
                actionPrompt.append("Enter your choice:");
                currentClient.sendMessage(actionPrompt.toString());

                // Receive choice from client
                Object response = currentClient.readMessage();
                int choice = Integer.parseInt(response.toString());

                switch (choice) {
                    case 1:
                        boolean cardSelected = false;
                        while (!cardSelected) {
                            StringBuilder promptMessage = new StringBuilder();
                            promptMessage.append("Choose a card to play (enter card number, or enter 0 to cancel):\n");
                            promptMessage.append(currentPlayer.getHandWithVisuals());
                            currentClient.sendMessage(promptMessage.toString());

                            response = currentClient.readMessage();
                            int cardIndex = Integer.parseInt(response.toString()) - 1;

                            if (cardIndex == -1) {
                                currentClient.sendMessage("Returning to the actions menu...");
                                cardSelected = true;
                            } else if (cardIndex >= 0 && cardIndex < currentPlayer.getHand().size()) {
                                Card playedCard = currentPlayer.getHand().get(cardIndex);

                                // Play the card and collect messages
                                List<String> actionMessages = playedCard.play(currentPlayer, opponent);

                                // Remove the card from the player's hand
                                currentPlayer.getHand().remove(cardIndex);

                                // Send messages to both clients
                                for (String msg : actionMessages) {
                                    currentClient.sendMessage(msg);
                                    opponentClient.sendMessage(msg); // Or selectively send messages
                                }

                                cardSelected = true;

                                // Update both players' status
                                currentClient.sendMessage(currentPlayer.getStatusWithBars());
                                opponentClient.sendMessage(opponent.getStatusWithBars());

                            } else {
                                currentClient.sendMessage("Invalid card selection. Try again.");
                            }
                        }
                        break;

                    case 2:
                        if (currentPlayer.getEnergy() >= 3) {
                            List<Card> newDrawnCards = currentPlayer.drawCards(1);
                            currentPlayer.spendEnergy(3);
                            currentClient.sendMessage("You drew: " + newDrawnCards.get(0).toString());
                        } else {
                            currentClient.sendMessage("Not enough energy to draw an additional card.");
                        }
                        break;

                    case 3:
                        turnEnded = true;
                        String endTurnMessage = currentPlayer.endTurn();
                        currentClient.sendMessage(endTurnMessage);
                        break;

                    case 4:
                        currentClient.sendMessage(currentPlayer.getCardInfo());
                        break;

                    default:
                        currentClient.sendMessage("Invalid choice. Try again.");
                        break;
                }

                // Automatically end turn if out of energy
                if (currentPlayer.getEnergy() <= 0) {
                    currentClient.sendMessage("Out of energy! Ending turn.");
                    turnEnded = true;
                    String endTurnMessage = currentPlayer.endTurn();
                    currentClient.sendMessage(endTurnMessage);
                }

                // Check for end of game
                if (endOfGame()) {
                    break;
                }
            }

            turnNumber++; // Increment the turn number

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void displayWinner() {
        // Find the player with health > 0
        for (Player player : players) {
            if (player.getHealth() > 0) {
                String winnerMessage = player.getName() + " wins the game!";
                // Inform both clients about the winner
                for (ClientHandler clientHandler : clientHandlers) {
                    try {
                        clientHandler.sendMessage(winnerMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
