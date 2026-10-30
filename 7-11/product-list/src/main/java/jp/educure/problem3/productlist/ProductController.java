package jp.educure.problem3.productlist;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    private final List<Product> products = List.of(
            new Product(1, "商品1", 1000.0, "商品1の説明です。"),
            new Product(2, "商品2", 2000.0, "商品2の説明です。"),
            new Product(3, "商品3", 3000.0, "商品3の説明です。"));

    @GetMapping("/product")
    public String list(Model model) {
        model.addAttribute("products", products);
        return "productList";
    }

    @GetMapping("/product/{id}")
    public String detail(@PathVariable int id, Model model) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(p -> {
                    model.addAttribute("product", p);
                    return "productDetail";
                })
                .orElse("error");
    }
}
