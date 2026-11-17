package jp.educure.problem3.eventperiod;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventPeriodController {

    @GetMapping("/event/input")
    public String input(Model model) {
        model.addAttribute("form", new EventPeriodForm());
        return "event/input";
    }

    @PostMapping("/event/submit")
    public String submit(@Validated @ModelAttribute("form") EventPeriodForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "event/input";
        }
        return "event/confirm";
    }
}
