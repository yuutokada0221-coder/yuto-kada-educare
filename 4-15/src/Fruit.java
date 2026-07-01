public class Fruit {
    private String name;
    private int price;

    public void setData(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void displayInfo() {
        System.out.println("果物: " + name);
        System.out.println("価格: " + price + "円");
    }
}
