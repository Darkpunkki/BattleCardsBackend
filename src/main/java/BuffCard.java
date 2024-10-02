import java.util.ArrayList;
import java.util.List;

public class BuffCard extends Card {
    private int attackBoost;

    public BuffCard(String name, int cost, int attackBoost, String desc) {
        super(name, cost, desc);
        this.attackBoost = attackBoost;
    }

    @Override
    public List<String> play(Player currentPlayer, Player opponent) {
        List<String> messages = new ArrayList<>();

        // Apply the attack boost to the current player
        currentPlayer.applyAttackBoost(attackBoost);

        // Add the message to the list
        messages.add(currentPlayer.getName() + " plays " + name + " and boosts attack by " + attackBoost + " for the next turn!");

        return messages;
    }

    @Override
    public void displayInfo() {
        // You may choose to return a String or handle this differently
    }
}
