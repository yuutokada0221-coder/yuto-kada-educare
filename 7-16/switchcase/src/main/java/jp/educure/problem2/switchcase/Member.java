package jp.educure.problem2.switchcase;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Member {

    private int id;
    private String name;
    /** GOLD / SILVER / BRONZE */
    private String rank;
}
