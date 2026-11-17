package jp.educure.problem3.eventperiod;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

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

    private static final String START_MSG = "開始日は本日以降の日付を入力してください";
    private static final String END_MSG = "終了日は開始日より後の日付でなければなりません";

    private org.springframework.test.web.servlet.ResultActions send(LocalDate start, LocalDate end) throws Exception {
        return mockMvc.perform(post("/event/submit")
                .param("startDate", start == null ? "" : start.toString())
                .param("endDate", end == null ? "" : end.toString()));
    }

    private LocalDate d(int plusDays) {
        return LocalDate.now().plusDays(plusDays);
    }

    @Test
    void inputFormIsShown() throws Exception {
        mockMvc.perform(get("/event/input")).andExpect(view().name("event/input"));
    }

    @Test
    void validPeriod() throws Exception {
        send(d(1), d(7)).andExpect(view().name("event/confirm"))
                .andExpect(content().string(containsString("開始日: " + d(1))))
                .andExpect(content().string(containsString("終了日: " + d(7))));
    }

    @Test
    void sameDay() throws Exception {
        send(d(3), d(3)).andExpect(view().name("event/input")).andExpect(content().string(containsString(END_MSG)));
    }

    @Test
    void pastStart() throws Exception {
        send(d(-1), d(1)).andExpect(content().string(containsString(START_MSG)))
                .andExpect(content().string(not(containsString(END_MSG))));
    }

    @Test
    void endBeforeStart() throws Exception {
        send(d(5), d(3)).andExpect(content().string(containsString(END_MSG)));
    }

    @Test
    void nulls() throws Exception {
        send(null, d(1)).andExpect(content().string(containsString("開始日を入力してください")));
        send(d(1), null).andExpect(content().string(containsString("終了日を入力してください")));
        send(null, null).andExpect(content().string(containsString("開始日を入力してください")))
                .andExpect(content().string(containsString("終了日を入力してください")));
    }
}
