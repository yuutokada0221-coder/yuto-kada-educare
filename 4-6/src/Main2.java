import java.util.ArrayList;
import java.util.Collections;

public class Main2 {
    public static void main(String[] args) {

        ArrayList<Integer> scores = new ArrayList<>();

        scores.add(85);
        scores.add(92);
        scores.add(78);
        scores.add(55);
        scores.add(43);

        System.out.println("点数リスト: " + scores);

        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        double average = (double) sum / scores.size();
        System.out.println("平均点: " + average);

        int max = Collections.max(scores);
        System.out.println("最高点: " + max);

        int count = 0;
        for (int score : scores) {
            if (score <= 60) {
                count++;
            }
        }
        System.out.println("不合格者数: " + count);
    }
}
