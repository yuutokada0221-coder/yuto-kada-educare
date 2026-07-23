public class Main4 {
    public static void main(String[] args) {
        Book book = new Book("Java入門");
        Library library = new Library();

        library.lendBook(book);
        
        library.lendBook(book);
        
        library.returnBook(book);
        
        library.lendBook(book);
    }
}
