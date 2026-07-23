public class Main3 {
    public static void main(String[] args) {
        BankAccount sm = new BankAccount();
        System.out.println("残高：" + sm.getBalance() +"円");

        sm.deposit(10000);
        System.out.println("残高：" + sm.getBalance() +"円");

        sm.withdraw(3000);
        System.out.println("残高：" + sm.getBalance() +"円");

        sm.withdraw(8000);



    }
    
}
