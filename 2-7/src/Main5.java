public class Main5 {
    public static void main(String[] args) {
        int notePrice = 280 * 3;
        int pencilPrice = 120 * 5;
        int eraserPrice = 100 * 2;
        int subTotal = notePrice + pencilPrice + eraserPrice;
        int tax = (int)(subTotal * 0.1);
        int total = subTotal + tax;
        int change = 2000 - total;
        System.out.println("小計：" + subTotal + "円");
        System.out.println("消費税：" + tax + "円");
        System.out.println("合計金額：" + total + "円");
        System.out.println("おつり：" + change + "円");
    }
}
