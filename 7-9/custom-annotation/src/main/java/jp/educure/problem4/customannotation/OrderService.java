package jp.educure.problem4.customannotation;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @LogExecution
    public void processOrder() {
        System.out.println("注文処理中...");
        System.out.println("注文処理完了");
    }
}
