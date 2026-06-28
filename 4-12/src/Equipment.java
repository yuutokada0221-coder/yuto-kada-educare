public class Equipment implements Comparable<Equipment> {

    private String name;
    private int attack;
    private int defense;

    public Equipment(String name, int attack, int defense) {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
    }

    public int getPower() {
        return attack + defense;
    }

    public Equipment clone() {
        return new Equipment(name, attack, defense);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Equipment)) {
            return false;
        }

        Equipment other = (Equipment) obj;

        return name.equals(other.name)
                && attack == other.attack
                && defense == other.defense;
    }

    @Override
    public int compareTo(Equipment other) {
        return other.getPower() - this.getPower();
    }

    @Override
    public String toString() {
        return name + "（攻撃力:" + attack + " 防御力:" + defense + "）";
    }
}
