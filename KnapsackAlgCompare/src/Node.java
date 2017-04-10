import java.util.ArrayList;

/**
 *
 * @author zachh_000
 */
public class Node implements Comparable<Node> {
    public int lvl;                     //level in tree (0 = root)
    public int val;                     //value
    public int wt;                      //weight
    public double bnd;                  //bound
    public ArrayList<Integer> itemSet;
    
    public Node() {
        this.lvl = 0;
        this.val = 0;
        this.wt = 0;
        this.bnd = 0;
        this.itemSet = new ArrayList<>();
    }
    
    public Node(Node parent) {
        this.lvl = parent.lvl + 1;
        this.val = parent.val;
        this.wt = parent.wt;
        this.bnd = 0;
        this.itemSet = new ArrayList<>(parent.itemSet);
    }
    
    public void getBound(int numItems, ArrayList<Item> items, int capacity) {
        int i = lvl;
        int setWeight = wt;
        bnd = val;
        
        if (wt >= capacity) {
            bnd = 0;
        }
        else {
            while (i < numItems && setWeight + items.get(i).wt <= capacity) {
                bnd += items.get(i).val;
                setWeight += items.get(i).wt;
                i++;
            }
            if (i < numItems)
                bnd += ((capacity - setWeight) * items.get(i).rat);
        }
    }
    
    public int compareTo(Node other) {
        double compare = other.bnd - this.bnd;
        if (compare == 0)
            return 0;
        else if (compare > 0)
            return 1;
        else
            return -1;
    }
}