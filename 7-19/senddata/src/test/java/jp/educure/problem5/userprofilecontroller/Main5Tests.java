package jp.educure.problem5.userprofilecontroller;

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

@SpringBootTest(classes = Main5.class)
@AutoConfigureMockMvc
class Main5Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void flow() throws Exception {
        mockMvc.perform(get("/profile/1"))
                .andExpect(content().string(containsString("田中太郎")))
                .andExpect(content().string(containsString("href=\"/profile/edit?id=1\"")));
        mockMvc.perform(get("/profile/edit").param("id", "1"))
                .andExpect(content().string(containsString("value=\"田中太郎\"")));

        var in = new String[] {"id", "1", "name", "鈴木次郎", "age", "25", "bio", "新しい自己紹介"};
        mockMvc.perform(post("/profile/confirm").param(in[0], in[1]).param(in[2], in[3]).param(in[4], in[5]).param(in[6], in[7]))
                .andExpect(content().string(containsString("プロフィール確認")))
                .andExpect(content().string(containsString("鈴木次郎")));
        mockMvc.perform(post("/profile/back-to-edit").param(in[0], in[1]).param(in[2], in[3]).param(in[4], in[5]).param(in[6], in[7]))
                .andExpect(content().string(containsString("プロフィール編集")))
                .andExpect(content().string(containsString("value=\"鈴木次郎\"")));
        mockMvc.perform(post("/profile/save").param(in[0], in[1]).param(in[2], in[3]).param(in[4], in[5]).param(in[6], in[7]))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("プロフィールが更新されました！")))
                .andExpect(content().string(containsString("鈴木次郎")));
        mockMvc.perform(get("/profile/1"))
                .andExpect(content().string(containsString("鈴木次郎")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("更新されました"))));
    }
}
