public class Warehouse<T> {
    private T item;

    public void store(T item) {
        this.item = item;
        System.out.println("商品追加: " + item);
    }

    public T retrieve() {
        T temp = item;
        item = null;
        System.out.println("取り出し: " + temp);
        return temp;
    }

    public boolean isEmpty() {
        return item == null;
    }
}
