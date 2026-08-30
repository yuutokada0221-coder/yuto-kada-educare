package jp.educure.problem4.paymentprocessor.payment;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final PaymentProcessor paymentProcessor;

    public OrderService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public void processOrder(double amount) {
        System.out.println("注文を受け付けました。支払いを処理中...");
        paymentProcessor.processPayment(amount);
        System.out.println("注文が正常に処理されました。");
    }
}
