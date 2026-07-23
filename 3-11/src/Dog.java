
public class Dog implements Animal {
    String name;
    int age;

    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public void makeSound() {
        System.out.println("ワンワン");
    }

    @Override
    public void eat() {
        System.out.println(this.name + "が餌を食べています");
    }
}
