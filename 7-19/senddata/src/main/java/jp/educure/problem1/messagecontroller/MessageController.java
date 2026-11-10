package jp.educure.problem1.messagecontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    @GetMapping("/message")
    public String form() {
        return "message-form";
    }

    @GetMapping("/message/result")
    public String result(@RequestParam("name") String name,
                         @RequestParam("message") String message,
                         Model model) {
        model.addAttribute("name", name);
        model.addAttribute("message", message);
        return "message-result";
    }
}
