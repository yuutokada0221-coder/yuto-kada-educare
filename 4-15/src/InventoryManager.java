import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryManager {
    private Map<String, Product> inventory = new HashMap<>();

    public void addProduct(String name, int stock) {
        Product product = new Product(name, stock);
        inventory.put(name, product);
        System.out.println(name + "を入荷しました");
    }

    private Optional<Product> findProduct(String name) {
        return Optional.ofNullable(inventory.get(name));
    }

    public void sellProduct(String name) {
        Optional<Product> product = findProduct(name);

        if (product.isPresent()) {
            product.get().decreaseStock();
            System.out.println(name + "の販売: 在庫から1個減少");
        } else {
            System.out.println("商品が見つかりません");
        }
    }
}
