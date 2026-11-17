package jp.educure.problem4.meetingroom;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/** 複数の項目を組み合わせてチェックする（相関項目チェック）。 */
public class MeetingRoomValidator implements Validator {

    private static final LocalTime OPEN = LocalTime.of(9, 0);
    private static final LocalTime CLOSE = LocalTime.of(18, 0);

    @Override
    public boolean supports(Class<?> clazz) {
        return MeetingRoomForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MeetingRoomForm form = (MeetingRoomForm) target;
        LocalDateTime start = form.getStartDateTime();
        LocalDateTime end = form.getEndDateTime();

        // 日時が揃っているときだけ日時の相関チェックを行う（未入力は @NotNull が検出する）
        if (start != null && end != null) {
            if (!end.isAfter(start)) {
                errors.rejectValue("endDateTime", "endBeforeStart", "終了日時は開始日時より後にしてください");
            } else if (Duration.between(start, end).compareTo(Duration.ofHours(3)) > 0) {
                errors.rejectValue("endDateTime", "tooLong", "最大利用時間は3時間までです");
            }
            if (!isBusinessTime(start, end)) {
                errors.rejectValue("startDateTime", "outOfHours", "平日9:00〜18:00の間で予約してください");
            }
        }

        // 会議室名と利用人数の組み合わせチェック
        String room = form.getRoomName();
        Integer people = form.getPeopleCount();
        if (room != null && people != null && people >= 1) {
            if (people >= 10 && "小会議室".equals(room)) {
                errors.rejectValue("roomName", "tooSmallRoom", "10人以上の場合は大会議室を選択してください");
            } else if (people <= 9 && "大会議室".equals(room)) {
                errors.rejectValue("roomName", "tooLargeRoom", "9人以下の場合は小会議室を選択してください");
            }
        }
    }

    // 平日 かつ 開始は9:00以降・終了は18:00まで（18:00ちょうどに終了する予約は可能）
    private boolean isBusinessTime(LocalDateTime start, LocalDateTime end) {
        return !isWeekend(start) && !isWeekend(end)
                && !start.toLocalTime().isBefore(OPEN)
                && !end.toLocalTime().isAfter(CLOSE)
                && !start.toLocalTime().isAfter(CLOSE)
                && !end.toLocalTime().isBefore(OPEN);
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
