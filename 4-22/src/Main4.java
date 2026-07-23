import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Main4 {
    public static void main(String[] args) {
        Gson gson = new Gson();
        
        try (Reader reader = new FileReader("excersise.json")) {
            Employee employee = gson.fromJson(reader, Employee.class);
            
            System.out.println("Name: " + employee.getName());
            System.out.println("Age: " + employee.getAge());
            System.out.println("Salary: " + (int) employee.getSalary());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
