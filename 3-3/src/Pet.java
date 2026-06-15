public class Pet {
    String name;
    int energy;

    public void eat(){
        this.energy += 10;
        System.out.println(this.name + "は食事をして元気になった！");
        
    }
    public void play(){
        this.energy -= 20;
        System.out.println(this.name + "は遊んで疲れた！");
        
    }
    public void showEnergy() {
        System.out.println(this.name + "の現在の体力は" + this.energy + "です。");
    }
}
