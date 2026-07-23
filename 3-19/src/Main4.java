public class Main4 {
    public static void main(String[] args) {
        System.out.println("100ドル = " + ExchangeRate.toJPY("USD", 100) + "円");

        ExchangeRate.updateRates(150.0, 160.0);

        System.out.println("100ドル = " + ExchangeRate.toJPY("USD", 100) + "円");
        System.out.println("80ユーロ = " + ExchangeRate.toJPY("EUR", 80) + "円");
    }
}