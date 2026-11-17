package jp.educure.problem4.meetingroom;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.test.web.servlet.ResultActions;
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

    private static LocalDate weekday() {
        LocalDate day = LocalDate.now().plusDays(7);
        while (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY) {
            day = day.plusDays(1);
        }
        return day;
    }

    private static LocalDate saturday() {
        LocalDate day = LocalDate.now().plusDays(7);
        while (day.getDayOfWeek() != DayOfWeek.SATURDAY) {
            day = day.plusDays(1);
        }
        return day;
    }

    private static String at(LocalDate day, int hour, int minute) {
        return day.atTime(hour, minute).toString();
    }

    private ResultActions send(String room, String start, String end, String people) throws Exception {
        return mockMvc.perform(post("/meetingroom/confirm")
                .param("roomName", room).param("startDateTime", start)
                .param("endDateTime", end).param("peopleCount", people));
    }

    private void ng(ResultActions actions, String message) throws Exception {
        actions.andExpect(view().name("meetingroom/input")).andExpect(content().string(containsString(message)));
    }

    @Test
    void inputFormIsShown() throws Exception {
        mockMvc.perform(get("/meetingroom")).andExpect(view().name("meetingroom/input"));
    }

    @Test
    void validReservations() throws Exception {
        LocalDate d = weekday();
        send("小会議室", at(d, 10, 0), at(d, 12, 0), "5").andExpect(view().name("meetingroom/confirm"))
                .andExpect(content().string(containsString("会議室名：小会議室")))
                .andExpect(content().string(containsString("利用開始：" + d + "T10:00")))
                .andExpect(content().string(containsString("利用人数：5人")));
        send("大会議室", at(d, 14, 0), at(d, 16, 0), "15").andExpect(view().name("meetingroom/confirm"));
        // 境界値: 9:00開始・18:00終了・ちょうど3時間・9人・10人・20人
        send("小会議室", at(d, 9, 0), at(d, 12, 0), "9").andExpect(view().name("meetingroom/confirm"));
        send("大会議室", at(d, 15, 0), at(d, 18, 0), "10").andExpect(view().name("meetingroom/confirm"));
        send("大会議室", at(d, 15, 0), at(d, 18, 0), "20").andExpect(view().name("meetingroom/confirm"));
    }

    @Test
    void requiredFields() throws Exception {
        LocalDate d = weekday();
        ng(send("", at(d, 10, 0), at(d, 12, 0), "5"), "会議室名は必須です");
        ng(send("小会議室", at(d, 10, 0), at(d, 12, 0), ""), "利用人数は必須です");
        ng(send("小会議室", "", at(d, 12, 0), "5"), "利用日時を正しく入力してください");
        ng(send("小会議室", at(d, 10, 0), "", "5"), "利用日時を正しく入力してください");
    }

    @Test
    void peopleRange() throws Exception {
        LocalDate d = weekday();
        ng(send("大会議室", at(d, 10, 0), at(d, 12, 0), "0"), "利用人数は1〜20人の間で入力してください");
        ng(send("大会議室", at(d, 10, 0), at(d, 12, 0), "21"), "利用人数は1〜20人の間で入力してください");
    }

    @Test
    void timeRules() throws Exception {
        LocalDate d = weekday();
        ng(send("小会議室", at(d, 12, 0), at(d, 10, 0), "3"), "終了日時は開始日時より後にしてください");
        ng(send("小会議室", at(d, 10, 0), at(d, 10, 0), "3"), "終了日時は開始日時より後にしてください");
        ng(send("小会議室", at(d, 10, 0), at(d, 13, 1), "3"), "最大利用時間は3時間までです");
    }

    @Test
    void businessHours() throws Exception {
        LocalDate d = weekday();
        String msg = "平日9:00〜18:00の間で予約してください";
        ng(send("小会議室", at(saturday(), 10, 0), at(saturday(), 12, 0), "3"), msg);
        ng(send("小会議室", at(d, 8, 59), at(d, 10, 0), "3"), msg);
        ng(send("小会議室", at(d, 16, 0), at(d, 18, 1), "3"), msg);
    }

    @Test
    void roomAndPeople() throws Exception {
        LocalDate d = weekday();
        ng(send("小会議室", at(d, 10, 0), at(d, 12, 0), "10"), "10人以上の場合は大会議室を選択してください");
        ng(send("大会議室", at(d, 10, 0), at(d, 12, 0), "9"), "9人以下の場合は小会議室を選択してください");
    }
}
