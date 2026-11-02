package jp.educure.problem5.advanced;

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
    void advancedShowsUserAndProducts() throws Exception {
        mockMvc.perform(get("/advanced"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bob")))
                .andExpect(content().string(containsString("User is active")))
                .andExpect(content().string(containsString("Product2")));
    }
}
