public class Main1 {
    public static void main(String[] args) {

        Calculator calc = new Calculator();

        try {
            int result = calc.divide(10, 0);
            System.out.println("10 ÷ 0 = " + result);

        } catch (ArithmeticException e) {
            System.out.println("10 ÷ 0 = エラー：" + e.getMessage());
        }
    }
}
