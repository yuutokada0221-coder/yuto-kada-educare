package jp.educure.problem2.switchcase;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = List.of(
                new Member(1, "山田太郎", "GOLD"),
                new Member(2, "鈴木花子", "SILVER"),
                new Member(3, "佐藤次郎", "BRONZE"),
                new Member(4, "田中三郎", "NONE"));
        model.addAttribute("members", members);
        return "members";
    }
}
