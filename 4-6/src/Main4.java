import java.util.ArrayList;
import java.util.HashSet;

public class Main4 {
    public static void main(String[] args) {

        ArrayList<String> allMonsters = new ArrayList<>();
        allMonsters.add("スライム");
        allMonsters.add("ドラゴン");
        allMonsters.add("ゴブリン");
        allMonsters.add("フェニックス");
        allMonsters.add("ユニコーン");

        ArrayList<String> battleHistory = new ArrayList<>();
        battleHistory.add("スライム");
        battleHistory.add("スライム");
        battleHistory.add("スライム");
        battleHistory.add("スライム");
        battleHistory.add("スライム");
        battleHistory.add("ドラゴン");
        battleHistory.add("ドラゴン");
        battleHistory.add("ゴブリン");
        battleHistory.add("ゴブリン");

        HashSet<String> discoveredMonsters = new HashSet<>(battleHistory);

        System.out.println("発見済みモンスター: " + discoveredMonsters);
        System.out.println("発見済みモンスター数: " + discoveredMonsters.size());

        int slimeCount = 0;
        for (String monster : battleHistory) {
            if (monster.equals("スライム")) {
                slimeCount++;
            }
        }

        System.out.println("スライムとの戦闘回数: " + slimeCount);

        ArrayList<String> undiscoveredMonsters = new ArrayList<>();

        for (String monster : allMonsters) {
            if (!discoveredMonsters.contains(monster)) {
                undiscoveredMonsters.add(monster);
            }
        }

        System.out.println("未発見のモンスター: " + undiscoveredMonsters);
    }
}