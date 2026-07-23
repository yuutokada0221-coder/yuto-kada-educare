public class Main2 {
    public static void main(String[] args) {
        Shape[] shapes = new Shape[2];
        shapes[0] = new Circle(5.0);
        shapes[1] = new Rectangle(5.0, 10.0);

        double totalArea = 0.0;

        for (Shape shape : shapes) {
            if (shape instanceof Circle) {
                System.out.printf("円の面積： %.1f%n", shape.getArea());
            } else if (shape instanceof Rectangle) {
                System.out.printf("長方形の面積： %.1f%n", shape.getArea());
            }
            totalArea += shape.getArea();
        }

        System.out.printf("総面積： %.1f%n", totalArea);
    }
}
