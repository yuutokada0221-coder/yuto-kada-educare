import java.io.*;
import java.net.*;

public class Main1 {
    public static void main(String[] args) throws IOException {

        String keyword = "Java";

        String searchURL = "https://www.google.com/search?q=" + URLEncoder.encode(keyword, "UTF-8");

        URL url = new URL(searchURL);
        
        try (InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8")) {
            int data;

            while ((data = reader.read()) != -1) {
                System.out.print((char) data);
            }
        }
    }
}