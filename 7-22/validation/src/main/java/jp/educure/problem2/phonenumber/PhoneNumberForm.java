package jp.educure.problem2.phonenumber;

import jakarta.validation.constraints.NotBlank;

public class PhoneNumberForm {

    @NotBlank(message = "電話番号は必須です")
    @ValidPhoneNumber
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
