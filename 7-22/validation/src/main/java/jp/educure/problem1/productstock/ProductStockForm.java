package jp.educure.problem1.productstock;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ProductStockForm {

    @NotNull(message = "在庫数は必須です")
    @Min(value = 1, message = "在庫数は1〜999の範囲で入力してください")
    @Max(value = 999, message = "在庫数は1〜999の範囲で入力してください")
    private Integer stock;

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
