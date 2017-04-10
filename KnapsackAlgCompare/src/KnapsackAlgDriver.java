import java.io.FileNotFoundException;

/**
 *
 * @author zachh_000
 */

public class KnapsackAlgDriver {
    public static void main(String[] args) throws FileNotFoundException {
        String[] easy20 = {"easy20.txt"};
        String[] easy50 = {"easy50.txt"};
        String[] easy200 = {"easy200.txt"};
        String[] hard50 = {"hard50.txt"};
        String[] hard200 = {"hard200.txt"};
        
        String[] myTest1 = {"myTest1.txt"};
        String[] myTest2 = {"myTest2.txt"};
        
        //KnapsackAlgCompare.main(easy20);
        System.out.println("easy50\n");
        KnapsackAlgCompare.main(easy50);
        System.out.println("\nhard50\n");
        KnapsackAlgCompare.main(hard50);
        System.out.println("\neasy200\n");
        KnapsackAlgCompare.main(easy200);
        //System.out.println("\nhard200\n");
        KnapsackAlgCompare.main(hard200);
        
        //KnapsackAlgCompare.main(myTest1);
        //KnapsackAlgCompare.main(myTest2);
    }
}