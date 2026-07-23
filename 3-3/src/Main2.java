public class Main2 {
    public static void main(String[] args) {
        Book sm = new Book();
        sm.setTitle("Java入門");
        
        sm.lend();
        sm.lend();
        sm.returnBook();
        sm.lend();


    }
}
