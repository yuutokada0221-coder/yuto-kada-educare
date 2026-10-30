package jp.educure.problem5.dashboard;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Customer customer = new Customer("山田 太郎", "taro.yamada@example.com");
        List<Item> items = List.of(
                new Item("商品A", 500),
                new Item("商品B", 1500),
                new Item("商品C", 3000));
        model.addAttribute("customer", customer);
        model.addAttribute("items", items);
        return "dashboard";
    }
}
