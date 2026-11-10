package jp.educure.problem2.calculatorcontroller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Main2.class)
@AutoConfigureMockMvc
class Main2Tests {

    @Autowired
    private MockMvc mockMvc;

    private void check(String a, String b, String op, String expected) throws Exception {
        mockMvc.perform(post("/calculator/result").param("num1", a).param("num2", b).param("operation", op))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expected)));
    }

    @Test
    void calculates() throws Exception {
        check("5", "3", "+", "Result: 8");
        check("10", "4", "-", "Result: 6");
        check("6", "7", "*", "Result: 42");
        check("20", "4", "/", "Result: 5");
        check("1", "3", "+", "Result: 4");
    }

    @Test
    void divisionByZero() throws Exception {
        check("5", "0", "/", "Error: Division by zero is not allowed.");
    }

    @Test
    void formIsShown() throws Exception {
        mockMvc.perform(get("/calculator"))
                .andExpect(content().string(containsString("name=\"operation\"")));
    }
}
