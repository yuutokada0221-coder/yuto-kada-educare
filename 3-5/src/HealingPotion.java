public class HealingPotion {
    public void heal(Player player) {
        // 引数で渡されたプレイヤーのHPを50加算
        player.hp += 50;
        System.out.println("回復ポーションを使用しました");
    }
}
