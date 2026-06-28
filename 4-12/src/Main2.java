public class Main2 {
    public static void main(String[] args) {

        Monster monster1 = new Monster("スライム", 5);
        Monster monster2 = new Monster("スライム", 5);
        Monster monster3 = new Monster("スライム", 10);

        if (monster1.equals(monster2)) {
            System.out.println(monster1 + " と " + monster2 + " は同じモンスターです");
        } else {
            System.out.println(monster1 + " と " + monster2 + " は違うモンスターです");
        }

        if (monster1.equals(monster3)) {
            System.out.println(monster1 + " と " + monster3 + " は同じモンスターです");
        } else {
            System.out.println(monster1 + " と " + monster3 + " は違うモンスターです");
        }
    }
}
