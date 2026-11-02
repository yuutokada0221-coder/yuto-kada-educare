package jp.educure.problem1.literal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("name", "山田太郎");
        model.addAttribute("age", 25);
        model.addAttribute("hobby", "読書");
        return "profile";
    }
}
