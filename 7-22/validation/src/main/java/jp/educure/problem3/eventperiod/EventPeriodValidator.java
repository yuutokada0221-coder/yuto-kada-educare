package jp.educure.problem3.eventperiod;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventPeriodValidator implements ConstraintValidator<ValidEventPeriod, EventPeriodForm> {

    @Override
    public boolean isValid(EventPeriodForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        boolean valid = true;

        LocalDate start = form.getStartDate();
        LocalDate end = form.getEndDate();

        if (start == null) {
            addError(context, "startDate", "開始日を入力してください");
            valid = false;
        } else if (start.isBefore(LocalDate.now())) {
            addError(context, "startDate", "開始日は本日以降の日付を入力してください");
            valid = false;
        }

        if (end == null) {
            addError(context, "endDate", "終了日を入力してください");
            valid = false;
        } else if (start != null && !end.isAfter(start)) {
            addError(context, "endDate", "終了日は開始日より後の日付でなければなりません");
            valid = false;
        }
        return valid;
    }

    private void addError(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
