public class Main3 {
    public static void main(String[] args) {
        String input = "Hello";
        String reversed = reverseString(input);

        System.out.println("入力: \"" + input + "\"");
        System.out.println("出力: \"" + reversed + "\"");
    }
    public static String reverseString(String text) {
        return new StringBuilder(text).reverse().toString();
    }
}