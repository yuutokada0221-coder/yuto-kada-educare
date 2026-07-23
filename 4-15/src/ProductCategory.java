public enum ProductCategory {
    FRUITS("果物"),
    VEGETABLES("野菜"),
    DAIRY("乳製品");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
