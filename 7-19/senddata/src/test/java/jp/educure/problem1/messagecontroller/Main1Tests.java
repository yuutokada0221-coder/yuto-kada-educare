package jp.educure.problem1.messagecontroller;

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

@SpringBootTest(classes = Main1.class)
@AutoConfigureMockMvc
class Main1Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void formIsShown() throws Exception {
        mockMvc.perform(get("/message"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("method=\"get\"")))
                .andExpect(content().string(containsString("name=\"message\"")));
    }

    @Test
    void resultShowsNameAndMessage() throws Exception {
        mockMvc.perform(get("/message/result").param("name", "John").param("message", "Hello!"))
                .andExpect(content().string(containsString("名前：John")))
                .andExpect(content().string(containsString("メッセージ：Hello!")));
    }

    @Test
    void japaneseAndEmpty() throws Exception {
        mockMvc.perform(get("/message/result").param("name", "田中").param("message", "こんにちは"))
                .andExpect(content().string(containsString("名前：田中")))
                .andExpect(content().string(containsString("メッセージ：こんにちは")));
        mockMvc.perform(get("/message/result").param("name", "").param("message", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("名前：<")));
    }
}
