public class Warrior extends Character {
    public String job = "戦士";

    // コンストラクタ（親クラスのコンストラクタを呼び出して初期化）
    public Warrior(String name, int hp) {
        super(name, hp);
    }

    // 親クラスのshowStatus()をオーバーライド
    public void showStatus() {
        // superキーワードを使って、親クラス（Character）のshowStatus()を呼び出す
        super.showStatus();
        
        // 独自のステータス（職業）を追加で表示する
        System.out.println("職業: " + this.job);
    }

    
}
