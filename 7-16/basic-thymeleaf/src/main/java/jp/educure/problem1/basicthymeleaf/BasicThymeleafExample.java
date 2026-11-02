package jp.educure.problem1.basicthymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicThymeleafExample {

    @GetMapping("/basic")
    public String basic() {
        return "basic";
    }
}
