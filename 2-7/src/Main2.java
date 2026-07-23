public class Main2 {
    public static void main(String[] args) {
        int price = 2500;
        double discount = 0.2;
        int discountPrice = (int)(price * discount);
        int totalPrice = price - discountPrice;
        System.out.println("割引額:" + discountPrice + "円");
        System.out.println("販売価格:" + totalPrice + "円");
    }
}
