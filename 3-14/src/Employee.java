public abstract class Employee {
    public String name;

    public Employee(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public abstract String getRole();

    public abstract int calculateSalary();
}
