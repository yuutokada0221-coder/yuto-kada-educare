package jp.educure.problem5.discountcampaign;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DiscountCampaignController {

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new DiscountCampaignValidator());
    }

    @GetMapping("/campaign")
    public String input(Model model) {
        model.addAttribute("form", new DiscountCampaignForm());
        return "campaign/input";
    }

    @PostMapping("/campaign")
    public String register(@Validated @ModelAttribute("form") DiscountCampaignForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "campaign/input";
        }
        return "campaign/confirm";
    }
}
