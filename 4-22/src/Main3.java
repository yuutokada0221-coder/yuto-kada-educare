import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class Main3 {
    public static void main(String[] args) {
        try {
            File file = new File("excersise.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            String title = doc.getElementsByTagName("title").item(0).getTextContent();
            String author = doc.getElementsByTagName("author").item(0).getTextContent();
            String priceStr = doc.getElementsByTagName("price").item(0).getTextContent();
            int price = Integer.parseInt(priceStr); // 価格は計算できるように数値(int)に変換

            Book book = new Book(title, author, price);

            System.out.println("タイトル: " + book.getTitle());
            System.out.println("著者: " + book.getAuthor());
            System.out.println("価格: " + book.getPrice());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
