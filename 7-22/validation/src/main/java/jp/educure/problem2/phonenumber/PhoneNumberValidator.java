package jp.educure.problem2.phonenumber;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    // 「XX-XXXX-XXXX」または「XXX-XXXX-XXXX」（2〜3桁-4桁-4桁、ハイフン必須）
    private static final Pattern PATTERN = Pattern.compile("^\\d{2,3}-\\d{4}-\\d{4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 未入力は @NotBlank に任せる
        if (value == null || value.isEmpty()) {
            return true;
        }
        return PATTERN.matcher(value).matches();
    }
}
