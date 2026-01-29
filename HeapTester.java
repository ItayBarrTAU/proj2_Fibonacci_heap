import java.util.*;

public class HeapTester {
    private static List<String> operationLog = new ArrayList<>();
    // מימוש מד התקדמות בסגנון TQDM
    public static void printProgressBar(int current, int total) {
        int percent = (int) ((double) current / total * 100);
        StringBuilder bar = new StringBuilder("[");
        int filled = percent / 2; 
        for (int i = 0; i < 50; i++) {
            if (i < filled) bar.append("=");
            else bar.append(" ");
        }
        bar.append("] ").append(percent).append("%");
        System.out.print("\r" + bar.toString());
        if (current == total) System.out.println();
    }

    public static void main(String[] args) {

        Heap heap = new Heap(true, true);
        heap.insert(325,"0");
        heap.deleteMin();
        

        testBinomialWithCuts();
        testMassiveHeapifyUp();
        runStressTestWithLog(100000);
    }

    /**
     * בדיקה 1: מוודאת ש-successive linking מתבצע בזמן decreaseKey (כש-lazyMelds=false).
     * זוהי דרישה מרכזית במטלה עבור "ערימה בינומית עם ניתוקים"[cite: 71, 72].
     */
    public static void testBinomialWithCuts() {
        System.out.println("--- Test 1: Binomial with Cuts (lazyMelds=false, lazyDecreaseKeys=true) ---");
        Heap heap = new Heap(false, true);
        int n = 64; 
        Heap.HeapItem[] items = new Heap.HeapItem[n];
        
        for (int i = 0; i < n; i++) {
            items[i] = heap.insert(i + 1000, "info" + i);
        }
        
        // הופך הכל לעץ אחד גדול בדרגה 6
        heap.deleteMin(); 
        
        System.out.println("Initial trees: " + heap.numTrees());

        // המשתמש מבצע הקטנות מפתח חוקיות (נשאר חיובי)
        // אם המימוש משתמש ב-meld פנימי, מספר העצים יישמר נמוך
        for (int i = n - 1; i >= n / 2; i--) {
            heap.decreaseKey(items[i], 500); 
        }

        System.out.println("Trees after cuts: " + heap.numTrees());
        if (heap.numTrees() > 20) {
            System.err.println("WARNING: High tree count! Did you use meld() inside decreaseKey as required? ");
        } else {
            System.out.println("SUCCESS: Successive linking triggered during cuts.");
        }
        System.out.println();
    }

    /**
     * בדיקה 2: בדיקת יציבות המבנה תחת heapifyUp מאסיבי (lazyDecreaseKeys=false).
     */
    public static void testMassiveHeapifyUp() {
        System.out.println("--- Test 2: Massive HeapifyUp Stability ---");
        Heap heap = new Heap(true, false);
        int n = 100;
        Heap.HeapItem[] items = new Heap.HeapItem[n];

        for (int i = 0; i < n; i++) {
            items[i] = heap.insert(1000 + i, "val" + i);
        }

        heap.deleteMin(); // איחוד לעצים גדולים

        // המשתמש דואג שהערך יישאר חיובי (1)
        for (int i = n - 1; i >= 1; i--) {
            int diff = items[i].key - 1; // יביא את המפתח ל-1
            heap.decreaseKey(items[i], diff);
        }

        System.out.println("Total Heapify Cost: " + heap.totalHeapifyCosts());
        if (heap.findMin().key != 1) {
            System.err.println("FAILED: Min key is not 1!");
        } else {
            System.out.println("SUCCESS: HeapifyUp finished correctly.");
        }
        System.out.println();
    }

    /**
     * בדיקה 3: ריצה אקראית מאסיבית עם מד התקדמות.
     */
    public static void runStressTestWithLog(int numOps) {
        Heap heap = new Heap(true, true);
        Random rand = new Random();
        Map<Integer, Heap.HeapItem> itemsMap = new HashMap<>(); // לשמירת אובייקטים ל-decreaseKey
        int idCounter = 0;

        System.out.println("Starting Stress Test...");

        try {
            for (int i = 0; i < numOps; i++) {
                int action = rand.nextInt(4); // 0: Insert, 1: DeleteMin, 2: DecreaseKey
                
                if (action == 0 || action == 3) {
                    int key = rand.nextInt(1000) + 1;
                    Heap.HeapItem item = heap.insert(key, "val" + idCounter);
                    itemsMap.put(idCounter, item);
                    operationLog.add("Insert(key=" + key + ", id=" + idCounter + ")");
                    idCounter++;
                } 
                else if (action == 1 && heap.size() > 0) {
                    operationLog.add("DeleteMin()");
                    int idToRemove = -1;
                    for (Map.Entry<Integer, Heap.HeapItem> entry : itemsMap.entrySet()) {
                        if (entry.getValue() == heap.findMin()) {
                            idToRemove = entry.getKey();
                            break;
                        }
                    }
                    if (idToRemove != -1) itemsMap.remove(idToRemove);
                    heap.deleteMin();
                } 
                else if (action == 2 && !itemsMap.isEmpty()) {
                    int id = rand.nextInt(idCounter);
                    if (itemsMap.containsKey(id)) {
                        Heap.HeapItem it = itemsMap.get(id);
                        int diff = rand.nextInt(it.key / 2 + 1);
                        operationLog.add("DecreaseKey(id=" + id + ", oldKey=" + it.key + ", diff=" + diff + ")");
                        heap.decreaseKey(it, diff);
                    }
                }

                // בדיקה פנימית בכל איטרציה
                if (!validateHeapStructure(heap)) {
                    throw new RuntimeException("Heap Validation Failed!");
                }
            }
            System.out.println("Success! All operations passed.");

        } catch (Exception e) {
            System.err.println("\n--- BUG DETECTED ---");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("--- Operation Dictionary (Reproduce Steps) ---");
            for (String op : operationLog) {
                System.err.println(op);
            }
        }
    }

    private static boolean validateHeapStructure(Heap h) {
        if (h.min == null) return h.size() == 0;
        
        Heap.HeapNode start = h.min.node;
        Heap.HeapNode curr = start;
        
        // בדיקת רשימת השורשים
        do {
            if (curr.item.key < h.min.key) {
                System.err.println("Violation: Found root " + curr.item.key + " smaller than min " + h.min.key);
                return false;
            }
            if (curr.parent != null) {
                System.err.println("Violation: Root node has a parent!");
                return false;
            }
            curr = curr.next;
        } while (curr != start);
        
        return true;
    }
}