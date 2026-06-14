public class Main4 {
    public static void main(String[] args) {
        Weapon weapon = new Weapon();

        weapon.setWeapon("伝説の剣", 100);

        for (int i = 0; i < 10; i++) {
            weapon.use();
        }

        weapon.use();

        weapon.repair();
        weapon.use();
    }
}
