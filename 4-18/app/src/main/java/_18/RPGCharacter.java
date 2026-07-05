package _18;

public class RPGCharacter {
    private int level;
    private int hp;
    private int mp;

    public RPGCharacter() {
        this.level = 1;
        this.hp = 100;
        this.mp = 50;
    }

    public void levelUp() {
        if (level >= 99) {
            throw new IllegalStateException("最大レベルに達しています");
        }
        level++;
        hp += 10;
        mp += 5;
    }

    // getterメソッドを追加
    public int getLevel() { return level; }
    public int getHP() { return hp; }
    public int getMP() { return mp; }
}
