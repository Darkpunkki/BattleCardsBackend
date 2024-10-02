public class DamageResult {
    private int damage;
    private String message;

    public DamageResult(int damage, String message) {
        this.damage = damage;
        this.message = message;
    }

    public int getDamage() {
        return damage;
    }

    public String getMessage() {
        return message;
    }
}
