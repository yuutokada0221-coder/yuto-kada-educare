import java.io.IOException;
import java.sql.SQLException;
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
        boolean running = true;

        while (running) {
            System.out.println("\n=== 英単語暗記アプリ ===");
            System.out.println("1: 単語を登録する");
            System.out.println("2: クイズを受ける");
            System.out.println("3: CSVファイルから単語をインポート");
            System.out.println("4: CSVファイルに単語をエクスポート");
            System.out.println("5: 単語を削除する");
            System.out.println("6: 単語を更新する");
            System.out.println("7: 終了する");
            System.out.print("番号を選択してください：");

            String input = scanner.nextLine();

            // DBエラー等が起きてもアプリが異常終了しないようにtry-catchで囲む
            try {
                switch (input) {
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
                        running = false;
                        break;
                    default:
                        // 1〜7以外の入力チェック
                        System.out.println("無効な選択です。1〜7の数字を入力してください。");
                        break;
                }
            } catch (Exception e) {
                System.out.println("エラーが発生しました: " + e.getMessage());
                System.out.println("処理を中断してメインメニューに戻ります。");
            }
        }
        cleanup();
    }

    private void registerWord() throws SQLException {
        System.out.println("英単語を入力してください：");
        String english = scanner.nextLine().trim();
        if (english.isEmpty()) {
            System.out.println("エラー: 空文字は登録できません。");
            return;
        }

        System.out.println("日本語訳を入力してください：");
        String japanese = scanner.nextLine().trim();
        if (japanese.isEmpty()) {
            System.out.println("エラー: 空文字は登録できません。");
            return;
        }

        Word word = new Word(english, japanese);
        wordManager.addWord(word);
        System.out.println("単語を登録しました。");
    }

    private void startQuiz() throws SQLException {
        int count = wordManager.getWordCount();
        if (count == 0) {
            System.out.println("登録された単語がありません。");
            return;
        }

        System.out.println("=== クイズを開始します ===");
        quiz.resetScore();
        quiz.setTotalQuestions(count);

        for (int i = 0; i < count; i++) {
            Word word = quiz.getRandomWord();
            if (word == null) break;

            System.out.println(word.getEnglish() + "の意味は？");
            String answer = scanner.nextLine();

            if (quiz.checkAnswer(word, answer)) {
                System.out.println("正解です！");
            } else {
                System.out.println("不正解です。正解は" + word.getJapanese() + "でした。");
            }
        }

        System.out.println("\nクイズ終了！");
        System.out.println(quiz.getTotalQuestions() + "問中" + quiz.getScore() + "問正解でした！");
    }

    private void importWords() throws IOException, SQLException {
        System.out.println("CSVファイル名を入力してください：");
        String filename = scanner.nextLine();
        int count = fileHandler.importFromCSV(filename, wordManager);
        System.out.println(count + "個の単語を読み込みました。");
    }

    private void exportWords() throws IOException, SQLException {
        System.out.println("CSVファイル名を入力してください：");
        String filename = scanner.nextLine();
        List<Word> words = wordManager.getWords();
        fileHandler.exportToCSV(words, filename);
        System.out.println(words.size() + "個の単語を保存しました。");
    }

    private void deleteWord() throws SQLException {
        System.out.println("削除する英単語を入力してください：");
        String english = scanner.nextLine();
        wordManager.deleteWord(english);
        System.out.println("単語を削除しました。");
    }

    private void updateWord() throws SQLException {
        System.out.println("更新する英単語を入力してください：");
        String english = scanner.nextLine();
        System.out.println("新しい日本語訳を入力してください：");
        String japanese = scanner.nextLine();
        wordManager.updateWord(english, japanese);
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
