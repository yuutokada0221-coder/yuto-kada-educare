package jp.educure.calculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {

    @GetMapping("/calculate")
    public String calculate(
            @RequestParam(value = "num1", required = false) String num1Str,
            @RequestParam(value = "num2", required = false) String num2Str) {

        if (num1Str == null || num1Str.isBlank() || num2Str == null || num2Str.isBlank()) {
            return buildHtml("エラー: パラメータが不足しています");
        }
        int num1;
        int num2;
        try {
            num1 = Integer.parseInt(num1Str.trim());
            num2 = Integer.parseInt(num2Str.trim());
        } catch (NumberFormatException e) {
            return buildHtml("エラー: 数値として認識できません");
        }

        int result = num1 + num2;
        return buildHtml(num1 + " + " + num2 + " = " + result);
    }

    private String buildHtml(String content) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>計算結果</title>
                    <style>
                        .result { color: blue; font-size: 24px; }
                    </style>
                </head>
                <body>
                    <div class="result">
                        %s
                    </div>
                </body>
                </html>
                """.formatted(content);
    }
}