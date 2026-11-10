package jp.educure.problem4.productcontroller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

    private final List<Product> products = List.of(
            new Product(1, "ノートパソコン", 120000),
            new Product(2, "スマートフォン", 80000),
            new Product(3, "イヤホン", 12000));

    @GetMapping("/products")
    public String list(Model model) {
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable("id") int id, Model model) {
        Product product = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "商品が見つかりません"));
        model.addAttribute("product", product);
        return "product-detail";
    }
}
