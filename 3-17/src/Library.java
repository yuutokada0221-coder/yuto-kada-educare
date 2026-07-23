public class Library {
    
    public void lendBook(Book book) {
        if (book.isLent()) {
            System.out.println("エラー： この本は既に貸し出し中です");
        } else {
            book.setLent(true);
            System.out.println("「" + book.getTitle() + "」を貸し出しました");
        }
    }

    public void returnBook(Book book) {
        if (!book.isLent()) {
            System.out.println("エラー： この本は貸し出しされていません");
        } else {
            book.setLent(false);
            System.out.println("「" + book.getTitle() + "」が返却されました");
        }
    }
}