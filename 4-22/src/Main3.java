class Book {
    private String title;
    private String author;
    private int price;

    public Book(String title, String author, int price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPrice() {
        return price;
    }
}

public class Main3 {
    public static void main(String[] args) {
        Book book = new Book("Javaプログラミング", "山田 太郎", 2800);

        System.out.println("タイトル: " + book.getTitle());
        System.out.println("著者: " + book.getAuthor());
        System.out.println("価格: " + book.getPrice());
    }
}
