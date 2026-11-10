package jp.educure.problem3.colorcontroller;

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

@SpringBootTest(classes = Main3.class)
@AutoConfigureMockMvc
class Main3Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void formIsShown() throws Exception {
        mockMvc.perform(get("/color"))
                .andExpect(content().string(containsString("method=\"get\"")))
                .andExpect(content().string(containsString("<option value=\"黄\">")));
    }

    @Test
    void resultShowsNameAndColor() throws Exception {
        mockMvc.perform(get("/color/result").param("name", "John").param("color", "赤"))
                .andExpect(content().string(containsString("こんにちは Johnさん")))
                .andExpect(content().string(containsString("あなたの好きな色は 赤 です")));
        mockMvc.perform(get("/color/result").param("name", "田中").param("color", "緑"))
                .andExpect(content().string(containsString("あなたの好きな色は 緑 です")));
    }
}
