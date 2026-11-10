package jp.educure.problem5.userprofilecontroller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileForm {
    private int id;
    private String name;
    private int age;
    private String bio;
}
