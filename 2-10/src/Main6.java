public class Main6 {
    public static void main(String[] args) {
        String day = "月";
        switch (day) {
            case "月":
                System.out.println("数学");
                break;
            case "火":
                System.out.println("英語");
                break;
            case "水":
                System.out.println("理科");
                break;
            case "木":
                System.out.println("社会");
                break;
            case "金":
                System.out.println("体育");
                break;
            case "土":
            case "日":
                System.out.println("休み");
                break;
            default:
                System.out.println("無効な曜日");
                break;
        }
    }
}