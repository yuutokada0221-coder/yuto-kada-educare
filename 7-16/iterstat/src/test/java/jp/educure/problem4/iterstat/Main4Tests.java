package jp.educure.problem4.iterstat;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void scheduleShowsStatusAndCount() throws Exception {
        mockMvc.perform(get("/schedule"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("件数: <span>5</span>件")))
                .andExpect(content().string(containsString("スタート")))
                .andExpect(content().string(containsString("ゴール")))
                .andExpect(content().string(containsString("background-color: #eef;")));
    }
}
