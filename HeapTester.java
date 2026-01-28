import java.util.*;

public class HeapTester {

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
        testBinomialWithCuts();
        testMassiveHeapifyUp();
        testRandomStressWithProgress();
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
    public static void testRandomStressWithProgress() {
        System.out.println("--- Test 3: 20,000 Random Operations Stress Test ---");
        Heap heap = new Heap(true, true); 
        Random rand = new Random();
        int iterations = 20000;
        List<Heap.HeapItem> activeItems = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            int op = rand.nextInt(10);
            
            if (op < 4) { // 40% Insert
                activeItems.add(heap.insert(rand.nextInt(100000) + 100, "data"));
            } 
            else if (op < 6 && heap.size() > 0) { // 20% deleteMin
                heap.deleteMin();
            } 
            else if (!activeItems.isEmpty()) { // 40% decreaseKey
                Heap.HeapItem it = activeItems.get(rand.nextInt(activeItems.size()));
                // המשתמש דואג להקטנה חוקית (תמיד משאיר לפחות 1)
                if (it.key > 10) {
                    heap.decreaseKey(it, rand.nextInt(5));
                }
            }

            if (i % 200 == 0) printProgressBar(i, iterations);
        }
        printProgressBar(iterations, iterations);

        System.out.println("Final Heap Status:");
        System.out.println("- Size: " + heap.size());
        System.out.println("- Trees: " + heap.numTrees());
        System.out.println("- Total Links: " + heap.totalLinks());
        System.out.println("- Total Cuts: " + heap.totalCuts());
        System.out.println("SUCCESS: Stress test passed without crashing.");
    }
}