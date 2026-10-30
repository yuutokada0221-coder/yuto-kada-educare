package jp.educure.problem3.productlist;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Main3Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShowsProducts() throws Exception {
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("商品1 - ¥1000.0")));
    }

    @Test
    void detailShowsProduct() throws Exception {
        mockMvc.perform(get("/product/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("商品2")));
    }

    @Test
    void unknownProductShowsError() throws Exception {
        mockMvc.perform(get("/product/99"))
                .andExpect(content().string(containsString("商品が見つかりませんでした")));
    }
}
