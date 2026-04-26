public class Main3 {
    public static void main(String[] args) {
        int height = 5;
        for (int i = 1; i <= height; i++) {
            for (int j = 1; j <= i * 2 - 1; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
