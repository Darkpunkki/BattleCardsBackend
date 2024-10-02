import java.util.ArrayList;
import java.util.List;

public class DefenseCard extends Card {
    private int defenseValue;

    public DefenseCard(String name, int cost, int defenseValue, String desc) {
        super(name, cost, desc);
        this.defenseValue = defenseValue;
    }

    @Override
    public List<String> play(Player currentPlayer, Player opponent) {
        List<String> messages = new ArrayList<>();

        // Apply defense to the current player and collect the message
        String defenseMessage = currentPlayer.addDefense(defenseValue);

        // Add messages to the list
        messages.add(currentPlayer.getName() + " plays " + name + " and gains " + defenseValue + " shield points!");
        messages.add(defenseMessage);

        return messages;
    }

    @Override
    public void displayInfo() {
        // You may choose to return a String or handle this differently
    }
}
