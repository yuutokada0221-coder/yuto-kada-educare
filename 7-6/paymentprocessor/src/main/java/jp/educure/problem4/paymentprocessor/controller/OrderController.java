package jp.educure.problem4.paymentprocessor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.educure.problem4.paymentprocessor.payment.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/process")
    public String process(@RequestParam double amount) {
        orderService.processOrder(amount);
        return "注文が正常に処理されました。";
    }
}
