import java.util.ArrayList;
import java.util.Collections;

public class GameCharacter {

    private String name;
    private ArrayList<Equipment> equipments;

    public GameCharacter(String name) {
        this.name = name;
        equipments = new ArrayList<>();
    }

    public void addEquipment(Equipment equipment) {
        equipments.add(equipment);
    }

    public void showEquipments() {
        System.out.println(name + "の装備:");

        for (int i = 0; i < equipments.size(); i++) {
            System.out.println((i + 1) + ". " + equipments.get(i));
        }
    }

    public void sortEquipments() {
        Collections.sort(equipments);
    }
}
