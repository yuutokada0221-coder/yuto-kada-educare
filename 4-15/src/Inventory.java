public class Inventory<T> {
    private T item;
    private final ProductCategory category;

    public Inventory(ProductCategory category) {
        this.category = category;
    }

    public void stockIn(T item) {
        this.item = item;
        System.out.println(category.getDisplayName() + "コーナー: " + item + "を入荷");
    }

    public T stockOut() {
        T temp = item;
        item = null;
        return temp;
    }

    public T getCurrentStock() {
        return item;
    }
}
