import java.util.ArrayList;
import java.util.List;

public class AttackCard extends Card {
    private int damage;

    public AttackCard(String name, int cost, int damage, String desc) {
        super(name, cost, desc);
        this.damage = damage;
    }

    @Override
    public List<String> play(Player currentPlayer, Player opponent) {
        List<String> messages = new ArrayList<>();

        // Calculate the final damage using the player's attack boost
        DamageResult damageResult = currentPlayer.calculateDamage(damage);
        int finalDamage = damageResult.getDamage();
        messages.add(damageResult.getMessage());

        messages.add(currentPlayer.getName() + " plays " + name + " and deals " + finalDamage + " damage!");

        // Opponent takes damage and we collect the message
        String opponentDamageMessage = opponent.takeDamage(finalDamage);
        messages.add(opponentDamageMessage);

        return messages;
    }

    @Override
    public void displayInfo() {
        // You may choose to return a String or handle this differently
    }
}

