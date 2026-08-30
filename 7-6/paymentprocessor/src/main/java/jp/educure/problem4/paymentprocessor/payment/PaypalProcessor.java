package jp.educure.problem4.paymentprocessor.payment;

import org.springframework.stereotype.Component;

@Component
public class PaypalProcessor implements PaymentProcessor {

    @Override
    public void processPayment(double amount) {
        System.out.println("PayPalで支払いを処理中: ¥" + amount);
    }
}