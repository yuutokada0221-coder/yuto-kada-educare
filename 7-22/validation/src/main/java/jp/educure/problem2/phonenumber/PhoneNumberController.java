package jp.educure.problem2.phonenumber;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PhoneNumberController {

    @GetMapping("/phone")
    public String input(Model model) {
        model.addAttribute("form", new PhoneNumberForm());
        return "phone/input";
    }

    @PostMapping("/phone/confirm")
    public String confirm(@Validated @ModelAttribute("form") PhoneNumberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "phone/input";
        }
        return "phone/confirm";
    }
}
