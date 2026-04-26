public class Main5 {
    public static void main(String[] args) {
        int selection = 1;
        switch (selection) {
            case 1:
                System.out.println("コーヒーを選びました");
                break;
            case 2:
                System.out.println("紅茶を選びました");
                break;
            case 3:
                System.out.println("ジュースを選びました");
                break;
            default:
                System.out.println("無効な選択です");
                break;
        }
    }
}