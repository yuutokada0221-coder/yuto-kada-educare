package jp.educure.problem5.inventoryservice.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jp.educure.problem5.inventoryservice.repository.ProductRepository;
import jp.educure.problem5.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final InventoryService inventoryService;
    private final ProductRepository productRepository;

    public ProductController(InventoryService inventoryService,
                              ProductRepository productRepository) {
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
    }

    @GetMapping("/check-availability/{productId}")
    public String checkProductAvailability(@PathVariable Long productId) {
        String product = productRepository.findProductById(productId);
        boolean isAvailable = inventoryService.checkAvailability(productId);

        if (isAvailable) {
            return product + " is available";
        } else {
            return product + " is out of stock";
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return "Invalid product ID: " + ex.getValue();
    }
}