public class Main1 {
    public static void main(String[] args){
      Pet sm = new Pet();
      sm.name = "ポチ";
      sm.energy = 100;
      System.err.println(sm.name + "生み出しました！");
      System.out.println("初期体力：" + sm.energy);
      sm.eat();
      sm.play();

    }
    
}
