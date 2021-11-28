import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class Candidate {

    private final Set<Product> products;
    private final double support;

    public Candidate(Set<Product> products, Set<Cart> allShoppingCarts) {
        this.products = products;
        this.support = this.calcSupport(allShoppingCarts);
    }

    public Candidate(){
        this.products = null;
        this.support = 1;
    }

    public Set<Product> products() {
        return Collections.unmodifiableSet(this.products);
    }

    private double calcSupport(Set<Cart> allShoppingCarts){
        double result = 0;

        for(Cart shoppingCart: allShoppingCarts){
            if (shoppingCart.products().containsAll(this.products)) {
                result++;
            }
        }
        return result / allShoppingCarts.size();
    }

    public double support() {
        return this.support;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate candidate = (Candidate) o;
        return this.products.containsAll(candidate.products) && candidate.products.containsAll(this.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(products);
    }
}
