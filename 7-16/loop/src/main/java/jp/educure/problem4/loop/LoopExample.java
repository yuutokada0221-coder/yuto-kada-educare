package jp.educure.problem4.loop;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoopExample {

    @GetMapping("/loop")
    public String loop(Model model) {
        model.addAttribute("items", List.of("Item1", "Item2", "Item3"));
        return "loop";
    }
}
