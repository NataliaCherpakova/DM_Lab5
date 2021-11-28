import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Main implements Runnable{

    private final Set<Product> products = new HashSet<>();
    private final Set<Cart> shoppingCarts = new HashSet<>();
    private long startTime;
    private int length;
    //private long total = 0;
    private Set<Candidate> candidates;

    public void run() {
        double minSupport = 0.01;

        /*********START TIME***********/
        startTime = System.currentTimeMillis();

        candidates = new HashSet<>();

        /******FIRST ONE_ELEMENT CANDIDATES************/

        for (Product product : this.products) {
            Set<Product> candidateProducts = new HashSet<>();
            candidateProducts.add(product);
            candidates.add(new Candidate(candidateProducts, this.shoppingCarts));
        }

        Set<Candidate> LowSupportCandidates = new HashSet<>();

        /****************MAIN CYCLE**************************/
        for (int i = 1; i < length; i++) {
            Set<Candidate> nextCandidates = new HashSet<>();
            Set<Candidate> finalCandidates = candidates;

            int j = 0;
            for (Candidate c : candidates) {

                /*********** PRINT TIME EVERY 100 CANDIDATE **********/
                if (++j % 100 == 0) {
                    long time = System.currentTimeMillis();
                    System.out.println(
                            "Cart of " + (i + 1) +
                                    " products: " + j + " of " +
                                    finalCandidates.size() + " done for: "+ (time - this.startTime) / 1000.0 + "s");
                }
                /****************************************************/
                products.forEach(p -> {
                    if (!c.products().contains(p)) {
                        Set<Product> newProducts = new HashSet<>(c.products());
                        newProducts.add(p);

                        Candidate newCandidate = new Candidate(newProducts, this.shoppingCarts);

                        boolean hasSubsetWithNotEnoughSupport =
                                LowSupportCandidates.contains(newCandidate);
                        if (!hasSubsetWithNotEnoughSupport) {
                            if (newCandidate.support() < minSupport) {
                                LowSupportCandidates.add(newCandidate);
                            } else {
                                nextCandidates.add(newCandidate);
                            }
                        }
                    }
                });
            }
            candidates = new HashSet<>(nextCandidates);
        }

        /********TIME TO STRING********/
        long time = System.currentTimeMillis();
        System.out.println("Allover time: " + (time - this.startTime) / 1000.0 + " s.");

        candidates.stream()
                .sorted(Comparator.comparingDouble(Candidate::support));

        /********CANDIDATE TO STRING********/
        CandToString();
    }

    private void CandToString(){
        Candidate c = new Candidate();
        double sup_max = 0;
        Candidate[] cand = candidates.toArray(new Candidate[candidates.size()]);
        for(int i = 0; i<cand.length; i++) {
            if (cand[i].support() > sup_max){
                c = cand[i];
                sup_max = c.support();
            }
        }
        System.out.println("Most popular products (in a cart):");
        System.out.println(c.support());
        Set<Product> products_cand = c.products();
        System.out.println("Products: ");
        for (Product product : products_cand) {
            System.out.println("  " + product.identifier() + " " +
                    product.description());
        }
        System.out.println();
        System.out.println("Support = " + c.support());
        System.out.println("===================================");

        //for(int i = 0; i<cand.length; i++){
        //}
        /*candidates.forEach(candidate -> {
            Set<Product> products = candidate.products();

            System.out.println("Products: ");
            for (Product product : products) {
                System.out.println("  " + product.identifier() + " " +
                                           product.description());
            }
            System.out.println();
            System.out.println("Support = " + candidate.support());
            System.out.println("===================================");
        });*/
    }


    /*************************************************PREPARE DATA*****************************************************/

    public void prepareData(int count) {
        File resources = this.getFileFromResources("table.xlsx");
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(resources);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Workbook workbook = StreamingReader.builder().
                open(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int kostil = 0;

        for (Iterator<Row> iter = sheet.rowIterator(); iter.hasNext(); ) {

            if (kostil++ == 0) {
                continue; //skip title :)
            }

            if (kostil++ > count) {
                break;
            }

            Row row = iter.next();
            String invoiceNo = row.getCell(0)
                    .getStringCellValue();
            if (invoiceNo.startsWith("C")) {
                continue;
            }

            String customerID = row.getCell(6)
                    .getStringCellValue();

            if (customerID.isEmpty()) {
                continue;
            }

            Optional<Cart> maybeShoppingCart =
                    this.shoppingCarts.stream()
                            .filter(shoppingCart -> shoppingCart.identifier()
                                    .equals(customerID))
                            .findFirst();

            Cart shoppingCart = maybeShoppingCart.orElseGet(
                    () -> new Cart(customerID));

            String productID = row.getCell(1)
                    .getStringCellValue();
            String description = row.getCell(2)
                    .getStringCellValue();
            this.products.add(new Product(productID, description));

            Cart updatedShoppingCart = shoppingCart.appendProduct(
                    new Product(productID, description));

            this.shoppingCarts.remove(updatedShoppingCart);
            this.shoppingCarts.add(updatedShoppingCart);
        }
    }

    /*************************************************READ FROM FILE***************************************************/

    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }

    public Set getProducts(){
        return this.products;
    }

    public Set getshoppingCarts(){
        return this.shoppingCarts;
    }

    public void setLength(int length){
        this.length = length;
    }
}
