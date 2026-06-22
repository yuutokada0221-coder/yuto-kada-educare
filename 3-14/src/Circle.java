public class Circle extends Shape {
    double radius; 
    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return this.radius * this.radius * 3.14;
    }
}
