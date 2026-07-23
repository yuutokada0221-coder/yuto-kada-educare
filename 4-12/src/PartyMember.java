import java.util.ArrayList;

public class PartyMember {

    private String name;
    private ArrayList<String> skills;

    public PartyMember(String name) {
        this.name = name;
        this.skills = new ArrayList<>();
    }

    public void addSkill(String skill) {
        skills.add(skill);
    }

    public PartyMember clone() {
        PartyMember copy = new PartyMember(this.name);
        copy.skills = new ArrayList<>(this.skills);
        return copy;
    }

    public void showSkills() {
        System.out.println("キャラクター「" + name + "」のスキル: " + skills);
    }
}
