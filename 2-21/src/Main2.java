public class Main2 {
    public static void calculateCircle(double radius) {
        double area = Math.PI * radius * radius;
        double circumference = 2 * Math.PI * radius;

        System.out.println("半径が" + radius + "の場合:");
        System.out.println("円の面積: " + area);
        System.out.println("円周: " + circumference);
    }
    public static void main(String[] args) {
        calculateCircle(5.0);
    }
}