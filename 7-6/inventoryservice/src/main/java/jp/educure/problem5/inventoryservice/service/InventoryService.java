package jp.educure.problem5.inventoryservice.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    // 偶数IDの商品は在庫あり、奇数IDは在庫なしと判定
    public boolean checkAvailability(Long productId) {
        if (productId == null) {
            return false;
        }
        return productId % 2 == 0; // 偶数IDなら在庫あり
    }
}