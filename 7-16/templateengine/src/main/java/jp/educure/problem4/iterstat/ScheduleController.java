package jp.educure.problem4.iterstat;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleController {

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("tasks", List.of("Java基礎", "SQL", "HTML/CSS", "Spring Boot"));
        return "schedule";
    }
}
