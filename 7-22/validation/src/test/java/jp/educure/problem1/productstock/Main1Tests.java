package jp.educure.problem1.productstock;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    private void ok(String stock, String shown) throws Exception {
        mockMvc.perform(post("/stock/confirm").param("stock", stock))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/confirm"))
                .andExpect(content().string(containsString("入力された在庫数: " + shown)));
    }

    private void ng(String stock, String message) throws Exception {
        mockMvc.perform(post("/stock/confirm").param("stock", stock))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/input"))
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void inputFormIsShown() throws Exception {
        mockMvc.perform(get("/stock"))
                .andExpect(view().name("stock/input"))
                .andExpect(content().string(containsString("在庫数入力フォーム")));
    }

    @Test
    void validValues() throws Exception {
        ok("500", "500");
        ok("1", "1");
        ok("999", "999");
    }

    @Test
    void outOfRange() throws Exception {
        ng("0", "在庫数は1〜999の範囲で入力してください");
        ng("1000", "在庫数は1〜999の範囲で入力してください");
        ng("-5", "在庫数は1〜999の範囲で入力してください");
    }

    @Test
    void required() throws Exception {
        ng("", "在庫数は必須です");
    }
}
