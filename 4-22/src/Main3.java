import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class Main3 {
    public static void main(String[] args) {
        try {
            File file = new File("excersise.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            
            Book book = (Book) unmarshaller.unmarshal(file);
            
            System.out.println("タイトル: " + book.getTitle());
            System.out.println("著者: " + book.getAuthor());
            System.out.println("価格: " + book.getPrice());
            
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
