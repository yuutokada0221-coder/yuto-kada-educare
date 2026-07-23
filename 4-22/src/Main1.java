import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main1 {
    public static void main(String[] args) {
        String fileName = "excersise.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("名前,年齢,職業");
            writer.println("山田,28,プログラマー");
            System.out.println("CSVファイルを作成しました: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line = br.readLine();
            
            if (line != null) {
                String[] elements = line.split(",");
                for (String element : elements) {
                    System.out.println(element);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
