package jp.educure.problem3.thobject;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @GetMapping("/books")
    public String books(Model model) {
        List<Book> books = List.of(
                new Book("Spring入門", "山田太郎", 2800, 5),
                new Book("Java実践ガイド", "鈴木一郎", 3200, 0));
        model.addAttribute("books", books);
        return "books";
    }
}
