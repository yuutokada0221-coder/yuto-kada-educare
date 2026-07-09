import java.io.IOException;
import java.util.Scanner;

public class VocabularyApp {
    private WordManager wordManager;
    private Quiz quiz;
    private FileHandler fileHandler;
    private Scanner scanner;

    public VocabularyApp() {
        this.wordManager = new WordManager();
        this.fileHandler = new FileHandler();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("=== 英単語暗記アプリ ===");
            System.out.println("1: 単語を登録する");
            System.out.println("2: クイズを受ける");
            System.out.println("3: CSVファイルから単語をインポート");
            System.out.println("4: CSVファイルに単語をエクスポート");
            System.out.println("5: 終了する");
            
            String choice = scanner.nextLine().trim();
            
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
                    System.out.println("終了します。");
                    return;
                default:
                    System.out.println("エラー：1〜5の数字を入力してください。");
            }
            System.out.println();
        }
    }

    private void registerWord() {
        System.out.println("英単語を入力してください：");
        String english = scanner.nextLine().trim();
        System.out.println("日本語訳を入力してください：");
        String japanese = scanner.nextLine().trim();

        // 空文字チェック（テストケース1-4, 1-5, 1-6対応）
        if (english.isEmpty() || japanese.isEmpty()) {
            System.out.println("エラー：英単語と日本語訳の両方を入力してください。");
            return;
        }

        wordManager.addWord(new Word(english, japanese));
        System.out.println("単語を登録しました。");
    }

    private void startQuiz() {
        // 登録単語なしチェック（テストケース2-4対応）
        if (wordManager.getWordCount() == 0) {
            System.out.println("登録された単語がありません。");
            return;
        }

        quiz = new Quiz(wordManager);
        System.out.println("=== クイズを開始します ===");

        // 登録単語数分ループ
        for (int i = 0; i < wordManager.getWordCount(); i++) {
            Word word = quiz.getRandomWord();
            if (word == null) break;

            System.out.println(word.getEnglish() + "の意味は？");
            String answer = scanner.nextLine().trim();

            if (quiz.checkAnswer(word, answer)) {
                System.out.println("正解です！");
            } else {
                System.out.println("不正解です。正解は" + word.getJapanese() + "でした。");
            }
            System.out.println();
        }

        System.out.println("クイズ終了！");
        System.out.println(quiz.getTotalQuestions() + "問中" + quiz.getScore() + "問正解でした！");
    }

    private void importWords() {
        System.out.println("CSVファイル名を入力してください：");
        String filename = scanner.nextLine().trim();

        try {
            int count = fileHandler.importFromCSV(filename, wordManager);
            System.out.println(count + "個の単語を読み込みました。");
        } catch (IOException e) {
            System.out.println("エラー：ファイルが存在しないか、読み込めません。");
        } catch (IllegalArgumentException e) {
            System.out.println("エラー：" + e.getMessage());
        }
    }

    private void exportWords() {
        if (wordManager.getWordCount() == 0) {
            System.out.println("エクスポートする単語がありません。");
            return;
        }
        
        System.out.println("CSVファイル名を入力してください：");
        String filename = scanner.nextLine().trim();

        try {
            int count = fileHandler.exportToCSV(wordManager.getWords(), filename);
            System.out.println(count + "個の単語を保存しました。");
        } catch (IOException e) {
            System.out.println("エラー：ファイルの保存に失敗しました。");
        }
    }

    public static void main(String[] args) {
        VocabularyApp app = new VocabularyApp();
        app.start();
    }
}