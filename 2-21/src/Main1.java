public class Main1 {
    public static void processString(String input) {
        String upper = input.toUpperCase();
        int count = input.length();
        System.out.println("大文字: " + upper);
        System.out.println("文字数: " + count);
    }

    public static void main(String[] args) {
        processString("Hello World");
    }
}
