public class BankAccount {
    private int balance;

    public BankAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    public int getBalance() {
        return this.balance;
    }

    public void deposit(int amount) {
        if (amount <= 0) {
            System.out.println("エラー：入金額は自然数（正の整数）を指定してください。");
            return;
        }
        this.balance += amount;
        System.out.println(amount + "円入金しました");
    }

    public void withdraw(int amount) {
        if (amount <= 0) {
            System.out.println("エラー：引き出し額は自然数（正の整数）を指定してください。");
            return;
        }
        if (this.balance < amount) {
            System.out.println("引き出しに失敗しました。残高が不足しています。");
            return;
        }
        this.balance -= amount;
        System.out.println(amount + "円引き出しました");
    }
}