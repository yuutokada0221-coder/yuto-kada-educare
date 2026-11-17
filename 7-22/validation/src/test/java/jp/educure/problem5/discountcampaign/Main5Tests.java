package jp.educure.problem5.discountcampaign;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.springframework.test.web.servlet.ResultActions;
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

    private ResultActions send(String name, String type, String value, int startPlus, int endPlus) throws Exception {
        LocalDate today = LocalDate.now();
        return mockMvc.perform(post("/campaign").param("name", name).param("type", type).param("value", value)
                .param("startDate", today.plusDays(startPlus).toString())
                .param("endDate", today.plusDays(endPlus).toString()));
    }

    private void ng(ResultActions actions, String message) throws Exception {
        actions.andExpect(view().name("campaign/input")).andExpect(content().string(containsString(message)));
    }

    @Test
    void inputFormIsShown() throws Exception {
        mockMvc.perform(get("/campaign")).andExpect(view().name("campaign/input"))
                .andExpect(content().string(containsString("割引キャンペーン登録")));
    }

    @Test
    void validCampaigns() throws Exception {
        send("春のセール", "fixed", "1000", 1, 15).andExpect(view().name("campaign/confirm"))
                .andExpect(content().string(containsString("キャンペーン名：春のセール")))
                .andExpect(content().string(containsString("割引種別：定額")))
                .andExpect(content().string(containsString("割引値：1000")));
        send("夏のセール", "percent", "20", 1, 20).andExpect(view().name("campaign/confirm"))
                .andExpect(content().string(containsString("割引種別：定率")));
    }

    @Test
    void nameAndValueRequired() throws Exception {
        ng(send("", "fixed", "500", 1, 10), "キャンペーン名は必須です");
        ng(send("テストキャンペーン", "fixed", "", 1, 10), "割引値は必須です");
    }

    @Test
    void fixedRules() throws Exception {
        ng(send("テストキャンペーン", "fixed", "150", 1, 10), "定額割引は100円単位で入力してください");
        ng(send("テストキャンペーン", "fixed", "15000", 1, 10), "定額割引は最大10,000円までです");
        send("テストキャンペーン", "fixed", "10000", 1, 10).andExpect(view().name("campaign/confirm"));
    }

    @Test
    void percentRules() throws Exception {
        ng(send("テストキャンペーン", "percent", "0", 1, 10), "定率割引は1〜50%の範囲で入力してください");
        ng(send("テストキャンペーン", "percent", "60", 1, 10), "定率割引は1〜50%の範囲で入力してください");
        send("テストキャンペーン", "percent", "50", 1, 10).andExpect(view().name("campaign/confirm"));
    }

    @Test
    void periodRules() throws Exception {
        ng(send("テストキャンペーン", "fixed", "500", -1, 10), "開始日は本日以降を指定してください");
        ng(send("テストキャンペーン", "fixed", "500", 10, 5), "終了日は開始日より後の日付にしてください");
        ng(send("テストキャンペーン", "fixed", "500", 5, 5), "終了日は開始日より後の日付にしてください");
        ng(send("テストキャンペーン", "fixed", "500", 1, 35), "キャンペーン期間は最大1ヶ月までです");
    }

    @Test
    void exactlyOneMonthIsAllowed() throws Exception {
        LocalDate start = LocalDate.now().plusDays(1);
        mockMvc.perform(post("/campaign").param("name", "x").param("type", "fixed").param("value", "500")
                .param("startDate", start.toString()).param("endDate", start.plusMonths(1).toString()))
                .andExpect(view().name("campaign/confirm"));
    }
}
