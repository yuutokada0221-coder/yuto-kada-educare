package jp.educure.problem5.discountcampaign;

import java.time.LocalDate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/** 割引種別・割引値・期間の組み合わせをチェックする（相関項目チェック）。 */
public class DiscountCampaignValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return DiscountCampaignForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DiscountCampaignForm form = (DiscountCampaignForm) target;

        // 割引値（未入力は @NotNull が検出する）
        Integer value = form.getValue();
        if (value != null) {
            if ("percent".equals(form.getType())) {
                if (value < 1 || value > 50) {
                    errors.rejectValue("value", "percentRange", "定率割引は1〜50%の範囲で入力してください");
                }
            } else {
                if (value < 100 || value % 100 != 0) {
                    errors.rejectValue("value", "fixedUnit", "定額割引は100円単位で入力してください");
                } else if (value > 10000) {
                    errors.rejectValue("value", "fixedMax", "定額割引は最大10,000円までです");
                }
            }
        }

        // キャンペーン期間
        LocalDate start = form.getStartDate();
        LocalDate end = form.getEndDate();
        if (start == null || start.isBefore(LocalDate.now())) {
            errors.rejectValue("startDate", "startPast", "開始日は本日以降を指定してください");
        }
        if (end == null || (start != null && !end.isAfter(start))) {
            errors.rejectValue("endDate", "endBeforeStart", "終了日は開始日より後の日付にしてください");
        } else if (start != null && end.isAfter(start.plusMonths(1))) {
            errors.rejectValue("endDate", "tooLong", "キャンペーン期間は最大1ヶ月までです");
        }
    }
}
