public class Book extends Product {

    String author;
    int stockQuantity;

    // コンストラクタ（商品名、価格、著者、在庫数を初期化）
    public Book(String name, int price, String author, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.author = author;
        this.stockQuantity = stockQuantity;
    }

    @Override
    public void displayInfo() {
        System.out.println("商品名：" + this.name);
        System.out.println("価格：" + this.price + "円");
        System.out.println("著者：" + this.author);
    }

    @Override
    public void checkStock() {
        if (this.stockQuantity >= 1) {
            System.out.println("在庫あり");
        } else {
            System.out.println("在庫なし");
        }
    }
}
