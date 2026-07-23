public class Product {
    private String name;
    private int stock;

    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public void decreaseStock() {
        if (stock > 0) {
            stock--;
        }
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return name + " (在庫: " + stock + "個)";
    }
}
