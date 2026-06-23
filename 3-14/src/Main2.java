public class Main2 {
    public static void main(String[] args) {
        Shape[] shapes = new Shape[2];
        shapes[0] = new Circle(5.0);
        shapes[1] = new Rectangle(5.0, 10.0);

        System.out.printf("円の面積： %.1f%n", shapes[0].getArea());
        System.out.printf("長方形の面積： %.1f%n", shapes[1].getArea());

        double totalArea = 0.0;
        for (int i = 0; i < shapes.length; i++) {
            totalArea += shapes[i].getArea();
        }

        System.out.printf("総面積： %.1f%n", totalArea);
    }
}
