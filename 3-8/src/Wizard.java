public class Wizard extends GameCharacter {
    public int mp;

    public Wizard(String name, int hp, int mp) {

        super(name, hp);
        
        this.mp = mp;
    }

    public void showStatus() {
        // 親クラスの名前とHPの表示処理を再利用
        super.showStatus();
        
        System.out.println("MP: " + this.mp);
    }
    
}
