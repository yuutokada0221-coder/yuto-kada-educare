import java.util.List;
import java.util.Scanner;

public class VocabularyApp {
    private WordManager wordManager;
    private Quiz quiz;
    private FileHandler fileHandler;
    private Scanner scanner;
    private DBManager dbManager;

    public VocabularyApp() {
        dbManager = new DBManager();
        wordManager = new WordManager(dbManager);
        quiz = new Quiz(wordManager);
        fileHandler = new FileHandler();
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("=== 英単語暗記アプリ ===");
            System.out.println("1: 単語を登録する");
            System.out.println("2: クイズを受ける");
            System.out.println("3: CSVファイルから単語をインポート");
            System.out.println("4: CSVファイルに単語をエクスポート");
            System.out.println("5: 単語を削除する");
            System.out.println("6: 単語を更新する");
            System.out.println("7: 終了する");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerWord();
                    break;
                case "2":
                    startQuiz();
                    break;
                case "3":
                    importWords();
                    break;
                case "4":
                    exportWords();
                    break;
                case "5":
                    deleteWord();
                    break;
                case "6":
                    updateWord();
                    break;
                case "7":
                    cleanup();
                    return;
                default:
                    break;
            }
        }
    }

    private void registerWord() {
        System.out.println("英単語を入力してください：");
        String english = scanner.nextLine();
        if (english.isEmpty()) {
            System.out.println("エラー: 入力が空です。");
            return;
        }
        
        System.out.println("日本語訳を入力してください：");
        String japanese = scanner.nextLine();
        if (japanese.isEmpty()) {
            System.out.println("エラー: 入力が空です。");
            return;
        }
        
        wordManager.addWord(new Word(english, japanese));
        System.out.println("単語を登録しました。");
    }

    private void startQuiz() {
        int count = wordManager.getWordCount();
        if (count == 0) {
            System.out.println("登録された単語がありません。");
            return;
        }
        
        System.out.println("=== クイズを開始します ===");
        quiz = new Quiz(wordManager);
        
        for (int i = 0; i < count; i++) {
            Word word = quiz.getRandomWord();
            System.out.println(word.getEnglish() + "の意味は？");
            String answer = scanner.nextLine();
            
            if (quiz.checkAnswer(word, answer)) {
                System.out.println("正解です！\n");
            } else {
                System.out.println("不正解です。正解は" + word.getJapanese() + "でした。\n");
            }
        }
        
        System.out.println("クイズ終了！");
        System.out.println(quiz.getTotalQuestions() + "問中" + quiz.getScore() + "問正解でした！\n");
    }

    private void importWords() {
        System.out.println("CSVファイル名を入力してください：");
        String filename = scanner.nextLine();
        int count = fileHandler.importFromCSV(filename, wordManager);
        if (count > 0) {
            System.out.println(count + "個の単語を読み込みました。");
        }
    }

    private void exportWords() {
        System.out.println("CSVファイル名を入力してください：");
        String filename = scanner.nextLine();
        List<Word> words = wordManager.getWords();
        int count = fileHandler.exportToCSV(words, filename);
        if (count > 0) {
            System.out.println(count + "個の単語を保存しました。");
        }
    }

    private void deleteWord() {
        System.out.println("削除する英単語を入力してください：");
        String english = scanner.nextLine();
        wordManager.deleteWord(english);
        System.out.println("単語を削除しました。");
    }

    private void updateWord() {
        System.out.println("更新する英単語を入力してください：");
        String english = scanner.nextLine();
        System.out.println("新しい日本語訳を入力してください：");
        String newJapanese = scanner.nextLine();
        wordManager.updateWord(english, newJapanese);
        System.out.println("単語を更新しました。");
    }

    private void cleanup() {
        dbManager.close();
        scanner.close();
    }

    public static void main(String[] args) {
        VocabularyApp app = new VocabularyApp();
        app.start();
    }
}
