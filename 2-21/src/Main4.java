import java.util.ArrayList;
import java.util.Collections;
public class Main4 {
    public static void processNumbers(int[] numbers) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int num : numbers) {
            list.add(num);
        }
        System.out.println("元のリスト: " + list);
        Collections.sort(list);
        System.out.println("ソート後: " + list);

        System.out.println("最大値: " + Collections.max(list));
        System.out.println("最小値: " + Collections.min(list));
    }
    public static void main(String[] args) {
        int[] numbers = {5, 2, 8, 1, 9};
        processNumbers(numbers);
    }
}