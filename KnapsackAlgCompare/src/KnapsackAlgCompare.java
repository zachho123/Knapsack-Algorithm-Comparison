import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Knapsack Algorithm Comparison
 * 
 * @author Zach Ho
 */

public class KnapsackAlgCompare {
    public void KS_Brute(int numItems, ArrayList<Item> items, int capacity) {
        // Generate list of every possible set of items represented by
        // a binary string of length numItems
        //long startTime = System.nanoTime();
        
        ArrayList<String> itemSets = getGrayCode(numItems);
        int maxVal = 0;
        int maxWeight = 0;
        int maxItemSetIdx = -1;
        
        // Iterate over the possible sets of items determining their
        // set values and set weights
        for (int i = 0; i < itemSets.size(); i++) {
            int setVal = 0;
            int setWeight = 0;
            String set = itemSets.get(i);
            
            // Iterate over the binary string representation of a single set
            for (int j = 0; j < set.length(); j++) {
                // If an item was taken, add its value/weight to the set total
                if (set.charAt(j) == '1') {
                    setVal += items.get(j).val;
                    setWeight += items.get(j).wt;
                }
            }
            // Update optimal set if the current set's weight is less than
            // the capacity and its value is greater than the previous max
            if (setWeight <= capacity && setVal > maxVal) {
                maxVal = setVal;
                maxWeight = setWeight;
                maxItemSetIdx = i;
            }
        }
        //long endTime = System.nanoTime();
        //long duration = endTime-startTime;
        //double seconds = (double)duration / 1000000000.0;
        //System.out.println("duration for enum = "+duration);
        
        // Obtain binary representation of optimal item set and print out
        // the items taken
        String bestSet = "";
        if (maxItemSetIdx > 0)
            bestSet = itemSets.get(maxItemSetIdx);
        
        // Print results
        System.out.println("Using Brute force the best feasible solution found: "
         + maxVal + " " + maxWeight);
        
        for (int k = 0; k < bestSet.length(); k++)
            if (bestSet.charAt(k) == '1')
                System.out.print(k+1 + " ");
        System.out.print("\n");
    }
    
    public void KS_Greedy(int numItems, ArrayList<Item> items, int capacity) {
        // Copy the list of items so that the original isn't altered by sort
        ArrayList<Item> itemsCopy = new ArrayList<>();
        for (int i = 0; i < numItems; i++)
            itemsCopy.add(new Item(items.get(i)));
        
        // Sort the copied list of items by decreasing value of value/weight
        // ratio to ensure you get the most value per unit weight
        Collections.sort(itemsCopy, Item.byRatio());
                
        int setItemCount = 0;
        int setVal = 0;
        int setWeight = 0;
        boolean maxWeight = false;
        
        //long startTime = System.nanoTime();
        // Take items until capacity is reached
        while (!maxWeight && setItemCount < numItems) {
            Item itemN = itemsCopy.get(setItemCount);
            if (setWeight + itemN.wt < capacity) {
                setVal += itemN.val;
                setWeight += itemN.wt;
                setItemCount++;
            }
            else {
                maxWeight = true;
            }
        }
        //long endTime = System.nanoTime();
        //long duration = endTime-startTime;
        //double seconds = (double)duration / 1000000000.0;
        //System.out.println("duration for greedy = "+duration);
        
        // Determine the set of items taken
        ArrayList<Item> maxSet = new ArrayList<>();
        for (int j = 0; j < setItemCount; j++)
            maxSet.add(itemsCopy.get(j));
        // Sort the set by increasing item index
        Collections.sort(maxSet, Item.byIndexAscend());
        
        // Print results
        System.out.println("Greedy solution (not necessarily optimal): " 
         +setVal+" "+setWeight);
        
        for (Item item : maxSet)
            System.out.print(item.id + " ");
        System.out.print("\n");
    }
    
    /* This approach was gathered from the code at
     * http://introcs.cs.princeton.edu/java/96optimization/Knapsack.java.html
     */
    public void KS_Dynamic(int numItems, ArrayList<Item> items, int capacity) {
        // table[n][w] = max profit for items 1..n with weight limit w
        // taken[n][w] = does solution for items 1..n take item N?
        int[][] table = new int[numItems+1][capacity+1];
        boolean[][] taken = new boolean[numItems+1][capacity+1];
        
        //long startTime = System.nanoTime();
        // Fill in both tables with appropriate values
        for (int n = 1; n <= numItems; n++) {
            Item itemN = items.get(n-1);
            for (int w = 1; w <= capacity; w++) {
                // Determine values for taking or not taking item N
                int dontTake = table[n-1][w];
                int take = itemN.wt <= w ? 
                 itemN.val + table[n-1][w-itemN.wt] : 0;
                // Take the max of taking or not taking item N and fill in
                // corresponding cell in taken table
                table[n][w] = Math.max(dontTake, take);
                taken[n][w] = (take > dontTake);
            }
        }
        //long endTime = System.nanoTime();
        //long duration = endTime-startTime;
        //double seconds = (double)duration / 1000000000.0;
        //System.out.println("duration for dynamic = "+duration);
        
        // Determine item set
        ArrayList<Item> itemSet = new ArrayList<>();
        int setVal = 0;
        int setWeight = 0;
        
        for (int n = numItems, w = capacity; n > 0; n--) {
            Item itemN = items.get(n-1);
            if (taken[n][w]) {
                itemSet.add(itemN);
                w -= itemN.wt;
                setVal += itemN.val;
                setWeight += itemN.wt;
            }
        }
        Collections.sort(itemSet, Item.byIndexAscend());
        
        // Print results
        System.out.println("Dynamic Programming solution: "+setVal+" "+setWeight);
        for (Item item : itemSet)
            System.out.print(item.id + " ");
        System.out.print("\n");
    }
    
    public void KS_BnB(int numItems, ArrayList<Item> items, int capacity) {
        Collections.sort(items, Item.byRatio());
        Node root = new Node();
        Node solution = new Node();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        
        root.getBound(numItems, items, capacity);
        queue.offer(root);
        
        //long startTime = System.nanoTime();
        // The queue shall consist of nodes which are the root of a promising
        // branch of the search space tree
        while(!queue.isEmpty()) {
            Node popped = queue.poll();            
            Item itemN = items.get(popped.lvl);
            
            if (popped.bnd > solution.val) {
                Node take = new Node(popped);
                take.val += itemN.val;
                take.wt += itemN.wt;
                take.itemSet.add(itemN.id);
            
                if (take.wt <= capacity && take.val > solution.val) {
                    solution = take;
                    printSolution(solution);
                }
                take.getBound(numItems, items, capacity);
                if (take.bnd > solution.val)
                    queue.offer(take);
            
                Node dontTake = new Node(popped);
                dontTake.getBound(numItems, items, capacity);
                if (dontTake.bnd > solution.val)
                    queue.offer(dontTake);
            }
        }
        //long endTime = System.nanoTime();
        //long duration = endTime-startTime;
        //double seconds = (double)duration / 1000000000.0;
        //System.out.println("duration for BnB = "+duration);
        
        //Print results
        Collections.sort(solution.itemSet);
        System.out.println("Using Branch and Bound the best feasible "
         +"solution found: "+solution.val+" "+solution.wt);
        for (Integer i : solution.itemSet)
            System.out.print(i + " ");
        System.out.print("\n");
    }
    
    public void printSolution(Node solution) {
        System.out.println("Current solution value: "+solution.val);
        System.out.println("Current solution weight: "+solution.wt);
        Collections.sort(solution.itemSet);
        for (Integer i : solution.itemSet)
            System.out.print(i + " ");
        System.out.println("\n");
    }
    
    public void printInfo(int numItems, ArrayList<Item> items, int capacity) {
        System.out.println("numItems: "+numItems);
        System.out.println("Index\tVal\tWeight\tRatio");
        for (Item item : items)
            System.out.println(item.id+"\t"+item.val+"\t"+item.wt+"\t"+item.rat);
        System.out.println("capacity: "+capacity);
    }
    
    public ArrayList getGrayCode(int n) {
        ArrayList<String> gray = new ArrayList<>();
        ArrayList<String> temp;
        
        if (n > 0) {
            temp = getGrayCode(n-1);
            for (int i = 0; i < temp.size(); i++)
                gray.add("0" + temp.get(i));
            for (int j = temp.size()-1; j >= 0; j--)
                gray.add("1" + temp.get(j));
        }
        else {
            gray.add("");
        }
        
        return gray;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        Scanner scan = new Scanner(file);
        KnapsackAlgCompare kac = new KnapsackAlgCompare();
        
        // Obtain list of items from formatted input text file
        int numItems = scan.nextInt();
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < numItems; i++)
            items.add(new Item(scan.nextInt(), scan.nextInt(), scan.nextInt()));
        int capacity = scan.nextInt();
        
        //kac.printInfo(numItems, items, capacity);
        //kac.KS_Brute(numItems, items, capacity);
        //kac.KS_Greedy(numItems, items, capacity);
        System.out.println();
        //kac.KS_Dynamic(numItems, items, capacity);
        System.out.println();
        kac.KS_BnB(numItems, items, capacity);
    }
}