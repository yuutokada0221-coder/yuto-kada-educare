public class Main3 {
    public static void main(String[] args) {

        Employee regular = new RegularEmployee("山田", 350000);
        Employee partTime = new PartTimeEmployee("鈴木", 1000, 120);

        System.out.println(regular.getRole() + "（" + regular.getName() + "）の給与： " + regular.calculateSalary() + "円");
        System.out.println(partTime.getRole() + "（" + partTime.getName() + "）の給与： " + partTime.calculateSalary() + "円");
    }
}