import java.util.Arrays;
public class Main4 {
    public static void main(String[] args) {
        int[] prices = {100, 200, 300};
        int[] quantities = {1, 2, 3};
        int[] totals = calculateTotals(prices, quantities);
        System.out.println("商品価格: " + Arrays.toString(prices) + ", 数量: " + Arrays.toString(quantities));
        System.out.println("合計金額: " + Arrays.toString(totals));
    }
    public static int[] calculateTotals(int[] prices, int[] quantities) {
        int[] totals = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            totals[i] = prices[i] * quantities[i] * 110 / 100;
        }
        return totals;
    }
}