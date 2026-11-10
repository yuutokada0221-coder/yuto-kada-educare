package jp.educure.problem4.productcontroller;

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

@SpringBootTest(classes = Main4.class)
@AutoConfigureMockMvc
class Main4Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void list() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(content().string(containsString("ノートパソコン")))
                .andExpect(content().string(containsString("（価格: 80000円）")))
                .andExpect(content().string(containsString("href=\"/products/3\"")));
    }

    @Test
    void detail() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(content().string(containsString("ノートパソコン")))
                .andExpect(content().string(containsString("価格: 120000円")))
                .andExpect(content().string(containsString("href=\"/products\"")));
        mockMvc.perform(get("/products/99")).andExpect(status().isNotFound());
    }
}
