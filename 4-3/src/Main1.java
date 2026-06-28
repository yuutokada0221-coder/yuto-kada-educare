public class Main1 {
    public static void main(String[] args) {
        String str1 = "Hello";
        String str2 = "World";

        System.out.println("元の文字列1: " + str1);
        System.out.println("元の文字列2: " + str2);

        String combined = str1 + str2;
        System.out.println("連結後: " + combined);

        String sub = combined.substring(2, 7);
        System.out.println("部分文字列(3-7): " + sub);
    }
}