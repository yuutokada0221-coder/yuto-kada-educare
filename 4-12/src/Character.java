public class Character implements Comparable<Character> {

    private String name;
    private int hp;
    private int attack;

    public Character(String name, int hp, int attack) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
    }

    public int getBattlePower() {
        return hp + attack;
    }

    @Override
    public int compareTo(Character other) {
        return other.getBattlePower() - this.getBattlePower();
    }

    @Override
    public String toString() {
        return name + "(HP:" + hp + " 攻撃力:" + attack + ") 戦闘力:" + getBattlePower();
    }
}
