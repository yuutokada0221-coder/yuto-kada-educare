package jp.educure.problem1.literal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("name", "佐藤花子");
        model.addAttribute("age", 28);
        model.addAttribute("city", "大阪");
        return "profile";
    }
}
