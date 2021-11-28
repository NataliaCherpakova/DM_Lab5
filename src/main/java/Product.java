import java.util.Objects;

public class Product {

    private final String identifier;
    private final String description;

    public Product(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public String identifier() {
        return this.identifier;
    }

    public String description() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return identifier.equals(product.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
