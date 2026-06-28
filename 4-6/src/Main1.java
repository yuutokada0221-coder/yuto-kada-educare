import java.util.ArrayList;

public class Main1 {
    public static void main(String[] args) {

        ArrayList<String> students = new ArrayList<>();

        students.add("田中");
        students.add("佐藤");
        students.add("鈴木");

        System.out.println("学生リスト: " + students);
        System.out.println("2番目の学生: " + students.get(1));
        System.out.println("学生数: " + students.size());
        System.out.println("鈴木さんは含まれていますか？: " + students.contains("鈴木"));
    }
}