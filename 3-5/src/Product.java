public class Product {
    public String name;
    public int price;

    // コンストラクタ（商品名と価格を初期化）
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    // 商品情報を表示するメソッド
    public void displayInfo() {
        System.out.println("商品名：" + this.name);
        System.out.println("価格：" + this.price + "円");
    }
    
}
