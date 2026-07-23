public class RegularEmployee extends Employee {
    public int baseSalary;
   
    public RegularEmployee(String name, int baseSalary) {
        super(name); 
        this.baseSalary = baseSalary;
    }
    @Override
    public String getRole() {
        return "正社員";
    }
    @Override
    public int calculateSalary() {
        return this.baseSalary;
    }
}
