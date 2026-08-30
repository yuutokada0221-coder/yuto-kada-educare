package jp.educure.problem5.inventoryservice.repository;

import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    // 実際のDBを使わないダミー実装
    public String findProductById(Long id) {
        return "Product " + id;
    }
}
