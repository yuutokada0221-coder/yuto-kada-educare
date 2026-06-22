public class Main2 {
    public static void main(String[] args) {
        Shape[] shapes = new Shape[2];
        shapes[0] = new Circle(5.0);
        shapes[1] = new Rectangle(5.0, 10.0);

        System.out.println("円の面積： " + shapes[0].getArea());
        System.out.println("長方形の面積： " + shapes[1].getArea());

        double totalArea = 0.0;
        for (int i = 0; i < shapes.length; i++) {
            totalArea += shapes[i].getArea();
        }
        System.out.println("総面積： " + totalArea);
    }
}
