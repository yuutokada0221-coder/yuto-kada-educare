public class Main2 {
    public static void main(String[] args) {
    Player player = new Player();
        player.name = "勇者";
        player.hp = 30;

        HealingPotion potion = new HealingPotion();

        player.displayInfo();
        potion.heal(player);      
        System.out.println("プレイヤーのHP: " + player.hp);   
    }
    
}
