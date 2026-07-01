public class Main4 {
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();

        manager.addProduct("バナナ", 5);

        manager.sellProduct("バナナ");

        System.out.print("存在しない商品の検索: ");
        manager.sellProduct("りんご");
    }
}
