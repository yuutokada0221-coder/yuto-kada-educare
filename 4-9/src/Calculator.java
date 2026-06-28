public class Calculator {

    public int divide(int a, int b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("0で割ることはできません");
        }

        return a / b;
    }
}