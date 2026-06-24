public class Main3 {
    public static void main(String[] args) {
        System.out.println("貸出可能な本: " + Library.getAvailableBooks() + "冊");

        Library library = new Library();
        
        library.borrowBook();
        System.out.println("1冊借りました");
        System.out.println("貸出可能な本: " + Library.getAvailableBooks() + "冊");

        library.returnBook();
        System.out.println("本を返却しました");
        System.out.println("貸出可能な本: " + Library.getAvailableBooks() + "冊");
    }
}