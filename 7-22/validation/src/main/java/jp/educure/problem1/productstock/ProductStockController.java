package jp.educure.problem1.productstock;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductStockController {

    @GetMapping("/stock")
    public String input(Model model) {
        model.addAttribute("form", new ProductStockForm());
        return "stock/input";
    }

    @PostMapping("/stock/confirm")
    public String confirm(@Validated @ModelAttribute("form") ProductStockForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "stock/input";
        }
        return "stock/confirm";
    }
}
