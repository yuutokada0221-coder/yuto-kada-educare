public class PartTimeEmployee extends Employee {
    int hourlyWage;
    int hoursWorked;
    public PartTimeEmployee(String name, int hourlyWage, int hoursWorked) {
        super(name); 
        this.hourlyWage = hourlyWage;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public String getRole() {
        return "パートタイム";
    }

    @Override
    public int calculateSalary() {
        return this.hourlyWage * this.hoursWorked;
    }
}
