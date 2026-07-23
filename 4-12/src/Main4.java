public class Main4 {
    public static void main(String[] args) {

        PartyMember original = new PartyMember("アリス");
        original.addSkill("ファイア");
        original.addSkill("ブリザード");

        PartyMember copy = original.clone();
        copy.addSkill("サンダー");

        System.out.println("深いコピー後にスキル追加");

        System.out.print("元の");
        original.showSkills();

        System.out.print("コピーした");
        copy.showSkills();
    }
}
