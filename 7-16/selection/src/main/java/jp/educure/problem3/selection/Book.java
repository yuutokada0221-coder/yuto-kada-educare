package jp.educure.problem3.selection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Book {

    private int id;
    private String title;
    private String author;
    private int price;
    private int stock;
}
