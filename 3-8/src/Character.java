public class Character {
    public String name;
    public int hp;

    public Character(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public void showStatus() {
        System.out.println("名前: " + this.name);
        System.out.println("HP: " + this.hp);
    }
}
