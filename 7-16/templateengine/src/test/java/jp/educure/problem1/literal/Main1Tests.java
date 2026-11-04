package jp.educure.problem1.literal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Main1.class)
@AutoConfigureMockMvc
class Main1Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("佐藤花子さんのプロフィール")))
                .andExpect(content().string(containsString("年齢: 28歳 / 住所: 大阪")))
                .andExpect(content().string(containsString("ようこそ、佐藤花子さん！")));
    }
}
