public class Main2 {
    public static void main(String[] args) {
        System.out.println("果物倉庫: ");
        Warehouse<String> fruitWarehouse = new Warehouse<>();

        fruitWarehouse.store("バナナ");
        fruitWarehouse.retrieve();
        System.out.println("空チェック: " + fruitWarehouse.isEmpty());

        System.out.println("\n日用品倉庫:");
        Warehouse<String> dailyWarehouse = new Warehouse<>();

        dailyWarehouse.store("ティッシュ");
        dailyWarehouse.retrieve();
        System.out.println("空チェック: " + dailyWarehouse.isEmpty());
    }
}
