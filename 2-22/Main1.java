import java.util.Scanner;

public class Main1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] englishWords = new String[20];
        String[] japaneseMeanings = new String[20];
        int wordCount = 0;
        while (true) {
            System.out.println("1: 単語を登録する");
            System.out.println("2: クイズを受ける");
            System.out.println("3: 終了する");
            System.out.print("番号を入力してください：");
            String menu = scanner.nextLine();

            if (menu.equals("1")) {
                if (wordCount >= 20) {
                    System.out.println("登録可能な単語数は20個までです");
                    System.out.println();
                    continue;
                }
                System.out.println("英単語を入力してください：");
                String english = scanner.nextLine();
                System.out.println("日本語訳を入力してください：");
                String japanese = scanner.nextLine();
                englishWords[wordCount] = english;
                japaneseMeanings[wordCount] = japanese;
                wordCount++;
                System.out.println("単語を登録しました");
                System.out.println();
            } else if (menu.equals("2")) {

                if (wordCount == 0) {
                    System.out.println("単語が登録されていません");
                    System.out.println();
                    continue;
                }
                int correctCount = 0;
                for (int i = 0; i < wordCount; i++) {
                    System.out.println(englishWords[i] + "の意味は？");
                    String answer = scanner.nextLine();
                    if (answer.equals(japaneseMeanings[i])) {
                        System.out.println("正解です！");
                        correctCount++;
                    } else {
                        System.out.println("不正解です");
                    }
                    System.out.println();
                }
                System.out.println("クイズ終了！");
                System.out.println(wordCount + "問中" + correctCount + "問正解でした！");
                System.out.println();
            } else if (menu.equals("3")) {
                System.out.println("終了します");
                break;
            } else {
                System.out.println("1-3の数字を入力してください");
                System.out.println();
            }
        }
        scanner.close();
    }
}