package jp.educure.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class MessageController {

    @GetMapping("/form")
    public String showForm() {
        return "forward:/form.html";
    }

    @PostMapping("/message")
    @ResponseBody
    public String message(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "color", required = false) String color) {

        String displayMessage = (message == null || message.isBlank())
                ? "No message provided" : message;
        String displayColor = (color == null || color.isBlank())
                ? "black" : color;

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Message Board</title>
                </head>
                <body>
                    <h1>Message Board</h1>
                    <div style="color: %s; font-size: 20px; padding: 10px; border: 1px solid black;">
                        %s
                    </div>
                    <p>Posted at: %s</p>
                </body>
                </html>
                """.formatted(displayColor, displayMessage, LocalDateTime.now());
    }
}
