package jp.educure.problem1.greeting;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetController {

    @GetMapping("/greet")
    public String greet(Model model) {
        model.addAttribute("message", "Hello, World!");
        return "greet";
    }
}
