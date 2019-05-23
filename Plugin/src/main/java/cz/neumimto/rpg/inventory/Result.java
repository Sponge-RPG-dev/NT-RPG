package cz.neumimto.rpg.inventory;

class Result {
    public int amount;
    public boolean consume;

    Result(int amount, boolean consume) {
        this.amount = amount;
        this.consume = consume;
    }
}