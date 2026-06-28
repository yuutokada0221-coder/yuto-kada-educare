public class Main5 {

    public static void main(String[] args) {

        Equipment sword = new Equipment("伝説の剣", 100, 0);
        Equipment shield = new Equipment("光の盾", 0, 80);
        Equipment shoes = new Equipment("魔法の靴", 10, 20);

        GameCharacter hero = new GameCharacter("勇者A");

        hero.addEquipment(sword);
        hero.addEquipment(shield);
        hero.addEquipment(shoes);

        System.out.println("ーーー キャラクター装備情報 ーーー");
        hero.showEquipments();
        System.out.println();

        Equipment copy = sword.clone();

        System.out.println("装備の複製テスト: "
                + (sword.equals(copy) ? "OK" : "NG"));

        System.out.println("装備の比較テスト: "
                + (sword.compareTo(shield) < 0 ? "OK" : "NG"));

        hero.sortEquipments();

        System.out.println("装備の強さソート: OK");
    }
}
