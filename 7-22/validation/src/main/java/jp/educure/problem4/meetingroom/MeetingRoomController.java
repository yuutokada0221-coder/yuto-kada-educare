package jp.educure.problem4.meetingroom;

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
public class MeetingRoomController {

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new MeetingRoomValidator());
    }

    @GetMapping("/meetingroom")
    public String input(Model model) {
        model.addAttribute("form", new MeetingRoomForm());
        return "meetingroom/input";
    }

    @PostMapping("/meetingroom/confirm")
    public String confirm(@Validated @ModelAttribute("form") MeetingRoomForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "meetingroom/input";
        }
        return "meetingroom/confirm";
    }
}
