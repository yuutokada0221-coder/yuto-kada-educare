package jp.educure.problem4.formhandling;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Main4Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void formIsShown() throws Exception {
        mockMvc.perform(get("/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("名前を入力してください")));
    }

    @Test
    void postShowsGreeting() throws Exception {
        mockMvc.perform(post("/greeting").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("こんにちは、")))
                .andExpect(content().string(containsString("John")));
    }
}
