public class Main2 {
    public static void main(String[] args) {
        int[] sales = {1000, 2000, 1500, 1800, 2200};
        int totalSales = 0;
        for (int i = 0; i < sales.length; i++) {
            totalSales += sales[i];
        }
        System.out.println("売上合計: " + totalSales + "円");
    }
}
