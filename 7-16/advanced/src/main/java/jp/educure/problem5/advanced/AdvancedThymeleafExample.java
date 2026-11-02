package jp.educure.problem5.advanced;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdvancedThymeleafExample {

    @GetMapping("/advanced")
    public String advanced(Model model) {
        Map<String, String> user = Map.of("name", "Bob", "role", "Admin", "status", "ACTIVE");
        List<Product> products = List.of(
                new Product("Product1", 100),
                new Product("Product2", 200),
                new Product("Product3", 300));
        model.addAttribute("user", user);
        model.addAttribute("products", products);
        return "advanced";
    }
}
