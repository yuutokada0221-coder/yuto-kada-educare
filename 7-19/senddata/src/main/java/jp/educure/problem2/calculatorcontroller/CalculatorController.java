package jp.educure.problem2.calculatorcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculatorController {

    @GetMapping("/calculator")
    public String form() {
        return "calculator";
    }

    @PostMapping("/calculator/result")
    public String calculate(@RequestParam("num1") int num1,
                            @RequestParam("num2") int num2,
                            @RequestParam("operation") String operation,
                            Model model) {
        switch (operation) {
            case "+" -> model.addAttribute("message", "Result: " + (num1 + num2));
            case "-" -> model.addAttribute("message", "Result: " + (num1 - num2));
            case "*" -> model.addAttribute("message", "Result: " + (num1 * num2));
            case "/" -> {
                if (num2 == 0) {
                    model.addAttribute("message", "Error: Division by zero is not allowed.");
                } else {
                    model.addAttribute("message", "Result: " + (num1 / num2));
                }
            }
            default -> model.addAttribute("message", "Error: Invalid operation.");
        }
        return "result";
    }
}
