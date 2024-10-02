import java.util.List;

public abstract class Card {
    protected String name;
    protected int cost;
    protected String desc;

    public Card(String name, int cost, String desc) {
        this.name = name;
        this.cost = cost;
        this.desc = desc;
    }

    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return "Effect: " + desc;
    }

    public abstract List<String> play(Player currentPlayer, Player opponent);

    @Override
    public String toString() {
        return name + " (Cost: " + cost + " Energy)";
    }

    public abstract void displayInfo();
}
