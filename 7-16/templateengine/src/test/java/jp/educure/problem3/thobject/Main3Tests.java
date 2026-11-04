package jp.educure.problem3.thobject;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void books() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("タイトル: Spring入門")))
                .andExpect(content().string(containsString("著者: 山田太郎")))
                .andExpect(content().string(containsString("価格: 2800円")))
                .andExpect(content().string(containsString("在庫あり（残り5冊）")))
                .andExpect(content().string(containsString("価格: 3200円")))
                .andExpect(content().string(containsString("在庫切れ")));
    }
}
