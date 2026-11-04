package jp.educure.problem5.fragment;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Main5.class)
@AutoConfigureMockMvc
class Main5Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeAndAbout() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Educure Shop")))
                .andExpect(content().string(containsString("ようこそ、Educure Shopへ！")))
                .andExpect(content().string(containsString("&copy; 2026 Educure Shop")));
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("会社概要")))
                .andExpect(content().string(containsString("プログラミングの教材を販売しています。")))
                .andExpect(content().string(containsString("&copy; 2026 Educure Shop")));
    }
}
