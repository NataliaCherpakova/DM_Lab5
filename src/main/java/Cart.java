import java.util.*;

public class Cart {

    private final String identifier;
    private final Map<Product, Integer> productsTable;

    public Cart(String identifier) {
        this.identifier = identifier;
        this.productsTable = new HashMap<>();
    }

    private Cart(String identifier, Map<Product, Integer> productsTable){
        this.identifier = identifier;
        this.productsTable = productsTable;
    }

    public Cart appendProduct(Product product) {
        Integer oldCount = this.productsTable.get(product);
        if (oldCount == null){
            oldCount = 1;
        }

        this.productsTable.put(product, oldCount+1);
        return new Cart(identifier, this.productsTable);
    }

    public String identifier() {
        return this.identifier;
    }

    public Map<Product, Integer> productsTable() {
        return Collections.unmodifiableMap(this.productsTable);
    }

    public Set<Product> products(){
        return new HashSet<>(this.productsTable.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cart that = (Cart) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
