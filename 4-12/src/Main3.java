import java.util.ArrayList;
import java.util.Collections;

public class Main3 {
    public static void main(String[] args) {

        ArrayList<Character> characters = new ArrayList<>();

        characters.add(new Character("勇者A", 100, 50));
        characters.add(new Character("魔法使いB", 70, 70));
        characters.add(new Character("戦士C", 80, 40));

        Collections.sort(characters);

        System.out.println("戦闘力順");

        for (Character character : characters) {
            System.out.println(character);
        }
    }
}
