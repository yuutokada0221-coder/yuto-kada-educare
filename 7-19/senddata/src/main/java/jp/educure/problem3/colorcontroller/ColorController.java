package jp.educure.problem3.colorcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ColorController {

    @GetMapping("/color")
    public String form() {
        return "color-select";
    }

    @GetMapping("/color/result")
    public String result(@RequestParam("name") String name,
                         @RequestParam("color") String color,
                         Model model) {
        model.addAttribute("name", name);
        model.addAttribute("color", color);
        return "color-result";
    }
}
