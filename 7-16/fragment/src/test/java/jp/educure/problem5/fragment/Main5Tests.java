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

@SpringBootTest
@AutoConfigureMockMvc
class Main5Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeAndAboutIncludeHeaderAndFooter() throws Exception {
        for (String url : new String[] {"/home", "/about"}) {
            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("サイトタイトル")))
                    .andExpect(content().string(containsString("2026 Sample Site")));
        }
    }
}
