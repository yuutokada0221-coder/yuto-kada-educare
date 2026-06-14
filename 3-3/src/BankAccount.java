public class BankAccount {
    int balance;

    public int getBalance(){
        return balance;
    }
    public void deposit(int amount){
        balance += amount;
        System.out.println(amount + "円預けました");
    }
    public void withdrow(int amount){
        if(balance >= amount){
           balance -= amount;
           System.out.println(amount + "円引き出しました");
        }else{
            System.out.println("残高が不足しています");

        }

    }
}
