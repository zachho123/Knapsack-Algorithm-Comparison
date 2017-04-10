import java.util.Comparator;

/**
 *
 * @author zachh_000
 */

public class Item {
    public int id;        //index
    public int val;       //value
    public int wt;        //weight
    public double rat;    //value/weight ratio
    
    public Item(int index, int val, int weight) {
        this.id = index;
        this.val = val;
        this.wt = weight;
        this.rat = (double)val / (double)weight;
    }
    
    public Item(Item other) {
        this.id = other.id;
        this.val = other.val;
        this.wt = other.wt;
        this.rat = other.rat;
    }
    
    public static Comparator<Item> byIndexAscend() {
        return new Comparator<Item>() {
            public int compare(Item i1, Item i2) {
               return i1.id - i2.id;
            }
        };
    }
    
    public static Comparator<Item> byRatio() {
        return new Comparator<Item>() {
            public int compare(Item i1, Item i2) {
                if (i1.rat < i2.rat)
                    return 1;
                else if (i1.rat == i2.rat)
                    return 0;
                else
                    return -1;
            }
        };
    }
}