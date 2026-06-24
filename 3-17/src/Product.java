public class Product {
    private String name;
    private int stock;

    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return this.name;
    }

    public int getStock() {
        return this.stock;
    }

    public void addStock(int amount) {
        if (amount <= 0) {
            System.out.println("エラー： 指定された値が不正です");
            return;
        }
        this.stock += amount;
        System.out.println(amount + "個追加しました");
    }

    public void removeStock(int amount) {
        if (amount <= 0) {
            System.out.println("エラー： 指定された値が不正です");
            return;
        }
        if (this.stock < amount) {
            System.out.println("エラー： 在庫が不足しています");
            return;
        }
        this.stock -= amount;
        System.out.println(amount + "個販売しました");
    }
}
