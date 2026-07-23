import java.util.function.Function;
import java.util.function.Predicate;

public class Main5 {
    public static void main(String[] args) {

        Function<Integer, Integer> multiplyByTwo = n -> n * 2;
        Function<Integer, Integer> subtractFive = n -> n - 5;
        Predicate<Integer> isPositive = n -> n > 0;
 
        int input = 8;

        int step1 = multiplyByTwo.apply(input);

        int step2 = subtractFive.apply(step1);

        if (isPositive.test(step2)) {
            System.out.println("正の数です");
        } else {
            System.out.println("負の数またはゼロです");
        }
    }
}
