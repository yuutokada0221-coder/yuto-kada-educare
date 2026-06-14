public class Book {
    // フィールド
    public int id;
    public String title;
    public String author;
    public int pages;

    // コンストラクタ1：タイトル、著者、IDを受け取る（ページ数は0で初期化）
    public Book(String title, String author, int id) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.pages = 0; // ページ数を0で初期化
    }

    // コンストラクタ2：すべてのフィールドを受け取る
    public Book(String title, String author, int id, int pages) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.pages = pages;
    }
}