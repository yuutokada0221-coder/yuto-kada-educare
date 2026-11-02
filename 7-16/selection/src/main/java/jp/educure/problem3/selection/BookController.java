package jp.educure.problem3.selection;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @GetMapping("/books")
    public String list(Model model) {
        List<Book> books = List.of(
                new Book(1, "Java入門", "山田太郎", 2980, 5),
                new Book(2, "Spring Boot実践", "鈴木花子", 3980, 0),
                new Book(3, "Thymeleaf基礎", "佐藤次郎", 2480, 12));
        model.addAttribute("books", books);
        return "books";
    }
}
