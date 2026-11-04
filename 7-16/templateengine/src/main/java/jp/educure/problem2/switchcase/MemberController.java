package jp.educure.problem2.switchcase;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/members")
    public String members(Model model) {
        List<Member> members = List.of(
                new Member("田中", "GOLD"),
                new Member("鈴木", "SILVER"),
                new Member("高橋", "BRONZE"),
                new Member("伊藤", "NONE"));
        model.addAttribute("members", members);
        return "members";
    }
}
