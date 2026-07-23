public class ExchangeRate {
    
    private static double usdRate = 145.0; 
    private static double eurRate = 160.0; 

    public static void updateRates(double newUsdRate, double newEurRate) {
        usdRate = newUsdRate;
        eurRate = newEurRate;
        System.out.println("レート更新");
    }

    public static int toJPY(String currency, double amount) {
        if (currency.equals("USD")) {
            return (int)(amount * usdRate); 
        } else if (currency.equals("EUR")) {
            return (int)(amount * eurRate); 
        } else {
            return -1; 
        }
    }
}
