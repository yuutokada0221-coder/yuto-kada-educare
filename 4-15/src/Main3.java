public class Main3 {
    public static void main(String[] args) {
        Inventory<String> fruits = new Inventory<>(ProductCategory.FRUITS);
        Inventory<String> vegetables = new Inventory<>(ProductCategory.VEGETABLES);
        Inventory<String> dairy = new Inventory<>(ProductCategory.DAIRY);

        fruits.stockIn("りんご");
        vegetables.stockIn("トマト");
        dairy.stockIn("牛乳");

        String shipped = fruits.stockOut();
        System.out.println("\n" + shipped + "を出荷しました");

        System.out.println("現在の在庫状態:");
        System.out.println("果物: " + (fruits.getCurrentStock() == null ? "なし" : fruits.getCurrentStock()));
        System.out.println("野菜: " + vegetables.getCurrentStock());
        System.out.println("乳製品: " + dairy.getCurrentStock());
    }
}