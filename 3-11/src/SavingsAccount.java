public class SavingsAccount implements BankAccount {
    String ownerName;
    int balance;

    public SavingsAccount(String ownerName, int initialBalance) {
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    @Override
    public void deposit(int amount) {
        if (amount <= 0) {
            System.out.println("無効な金額です。1円以上の金額を指定してください。");
            return;
        }
        this.balance += amount;
        System.out.println(amount + "円入金しました");
    }

    @Override
    public void withdraw(int amount) {
        if (amount <= 0) {
            System.out.println("無効な金額です。1円以上の金額を指定してください。");
            return;
        }
        if (this.balance < amount) {
            System.out.println("残高不足です。出金できません。");
            return;
        }
        this.balance -= amount;
        System.out.println(amount + "円出金しました");
    }

    @Override
    public int getBalance() {
        return this.balance;
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("口座名義人： " + this.ownerName);
        System.out.println("残高： " + this.balance);
    }
}