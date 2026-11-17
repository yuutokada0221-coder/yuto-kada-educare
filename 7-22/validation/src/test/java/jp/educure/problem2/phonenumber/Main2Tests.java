package jp.educure.problem2.phonenumber;

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

@SpringBootTest(classes = Main2.class)
@AutoConfigureMockMvc
class Main2Tests {

    @Autowired
    private MockMvc mockMvc;

    private void ok(String phone) throws Exception {
        mockMvc.perform(post("/phone/confirm").param("phoneNumber", phone))
                .andExpect(view().name("phone/confirm"))
                .andExpect(content().string(containsString("入力された電話番号: " + phone)));
    }

    private void ng(String phone, String message) throws Exception {
        mockMvc.perform(post("/phone/confirm").param("phoneNumber", phone))
                .andExpect(view().name("phone/input"))
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void inputFormIsShown() throws Exception {
        mockMvc.perform(get("/phone")).andExpect(view().name("phone/input"));
    }

    @Test
    void validNumbers() throws Exception {
        ok("090-1234-5678");
        ok("03-1234-5678");
    }

    @Test
    void invalidFormats() throws Exception {
        for (String phone : new String[] {"09012345678", "090-abcd-5678", "090-123-567", "090-12345-5678"}) {
            ng(phone, "正しい電話番号形式で入力してください");
        }
    }

    @Test
    void required() throws Exception {
        ng("", "電話番号は必須です");
    }
}
