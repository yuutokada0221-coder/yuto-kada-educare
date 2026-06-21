public class Main5 {
    public static void main(String[] args) {
        SavingsAccount account = new SavingsAccount("山田太郎", 10000);
        
        account.deposit(5000);
        account.withdraw(3000);
        account.displayAccountInfo();
    }
}