package jp.educure.problem4.iterstat;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleController {

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("schedules", List.of(
                "朝礼", "企画会議", "ランチミーティング", "資料作成", "終礼"));
        return "schedule";
    }
}
