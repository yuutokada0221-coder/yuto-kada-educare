public class Library {
    static int availableBooks = 100;

    public void borrowBook() {
        if (availableBooks <= 0) {
            System.out.println("エラー: 貸出可能な本がありません");
        } else {
            availableBooks--;
        }
    }

    public void returnBook() {
        availableBooks++;
    }

    public static int getAvailableBooks() {
        return availableBooks;
    }
}
