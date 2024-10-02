import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int health;
    private int shield; // Additional defense points
    private int energy;
    private Deck deck;
    private List<Card> hand;
    private int attackBoost = 0; // Temporary attack boost for next turn
    private EnergyReplenishmentStrategy energyReplenishmentStrategy;

    public Player(String name, EnergyReplenishmentStrategy energyReplenishmentStrategy) {
        this.name = name;
        this.health = 40; // Starting health
        this.shield = 0;  // Shield starts at 0
        this.energy = 15; // Starting energy
        this.deck = new Deck();
        this.hand = new ArrayList<>();
        this.energyReplenishmentStrategy = energyReplenishmentStrategy;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }


    // Add defense points to shield
    public String addDefense(int defenseValue) {
        this.shield += defenseValue;
        return name + " gains " + defenseValue + " shield points! Current shield: " + shield;
    }


    public String getStatusWithBars() {
        return "Health: " + health + "\n" +
                "Shield: " + shield + "\n" +
                "Energy: " + energy + "\n";
    }

    public String getHandWithVisuals() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            sb.append((i + 1)).append(". ").append(hand.get(i)).append("\n");
        }
        return sb.toString();
    }

    public String getCardInfo() {
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card.getName()).append(": ").append(card.getDesc()).append("\n");
        }
        return sb.toString();
    }

    // Take damage, prioritizing shield reduction first
    public String takeDamage(int amount) {
        StringBuilder message = new StringBuilder();
        if (shield > 0) {
            if (amount >= shield) {
                amount -= shield;
                message.append(name).append("'s shield is broken!\n");
                shield = 0;
            } else {
                shield -= amount;
                message.append(name).append(" takes ").append(amount).append(" damage to the shield. Remaining shield: ").append(shield);
                return message.toString();
            }
        }
        health -= amount;
        if (health < 0) {
            health = 0;
        }
        message.append(name).append(" takes ").append(amount).append(" damage! Remaining health: ").append(health);
        return message.toString();
    }


    public String replenishEnergy(int turnNumber) {
        int energyGained = energyReplenishmentStrategy.getEnergyForTurn(turnNumber);
        energy += energyGained;
        return name + " now has " + energy + " energy after replenishing " + energyGained + " energy on turn " + turnNumber;
    }


    public List<Card> drawCards(int numberOfCards) {
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            if (deck.hasCards()) {
                Card drawnCard = deck.drawCard();
                hand.add(drawnCard);
                drawnCards.add(drawnCard);
            } else {
                // You can handle this message in the calling method
                // Optionally, throw an exception or return a special value
                break;
            }
        }
        return drawnCards;
    }




    public List <Card> getHand() {
        return hand;
    }

    public Card playCard(int cardIndex, Player opponent) {
        Card card = hand.get(cardIndex);
        if (energy >= card.getCost()) {
            spendEnergy(card.getCost());
            card.play(this, opponent);
            hand.remove(cardIndex);
            return card;
        } else {
            throw new IllegalStateException("Not enough energy to play this card.");
        }
    }



    public String endTurn() {
        return name + " ends their turn.";
    }


    public void spendEnergy(int i) {
        energy -= i;
    }


    public String applyAttackBoost(int attackBoost) {
        this.attackBoost = attackBoost;
        return name + "'s attack is boosted by " + attackBoost + " for the next turn.";
    }


    // Method to calculate damage, including the attack boost
    public DamageResult calculateDamage(int baseDamage) {
        int totalDamage = baseDamage + attackBoost;
        String message = name + "'s attack deals " + totalDamage + " damage (including " + attackBoost + " bonus damage).";
        attackBoost = 0; // Reset attack boost after use
        return new DamageResult(totalDamage, message);
    }



    public int getEnergy() {
        return energy;
    }
}
