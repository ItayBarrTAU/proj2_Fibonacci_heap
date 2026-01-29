/**
 * Heap
 *
 * An implementation of Fibonacci heap over positive integers 
 * with the possibility of not performing lazy melds and 
 * the possibility of not performing lazy decrease keys.
 *
 */
public class Heap
{
    public int heapify = 0;
    public int cuts = 0;
    public int links = 0;
    public int markedNodes = 0;
    public int numOfTrees = 0;
    public int size = 0;

    public final boolean lazyMelds;
    public final boolean lazyDecreaseKeys;
    public HeapItem min;
    
    /**
     *
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys)
    {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        this.min = null;
        
    }

    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys, HeapItem first )
    {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        this.min = first;
        this.size = 1;
        this.numOfTrees = 1;

    }

    /**
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapItem insert(int key, String info) 
    {   
        HeapItem newItem = new HeapItem(key, info);
        Heap newHeap = new Heap(this.lazyMelds, this.lazyDecreaseKeys, newItem);
        this.meld(newHeap);

        return newItem; 
    }

    /**
     * 
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapItem findMin()
    {
        return min; 
    }

    /**
     * 
     * Delete the minimal item.
     *
     */

    


    public void deleteMin()
    {
        
        if (size == 0){
            throw new RuntimeException("nothing to delete");
        }
        if (size == 1){
            size --;
            numOfTrees --;
            min = null;
            return;
        }


        if (min.node.child != null) {
            HeapNode child = min.node.child;
            HeapNode curr = child;
            // ניתוק הקשר לאבא ואיפוס סימון
            do {
                curr.parent = null;
                if (curr.marked) {
                    curr.marked = false;
                    markedNodes--;
                }
                curr = curr.next;
                
            } while(curr != child);
            numOfTrees += min.node.rank -1;
            concatenate(min.node, child);

        }

        // 2. הסרת צומת המינימום מהרשימה
        HeapNode nextNode = min.node.next;
        min.node.prev.next = min.node.next;
        min.node.next.prev = min.node.prev;

        if (min.node == nextNode) {
            min = null;
        } else {
            min = nextNode.item;
    
        }

        size--;

        

        HeapNode c = min.node;
        HeapItem start = min;
        do{

            if (min.key > c.item.key){
                min = c.item;
            }
            c = c.next;
        }while ( c != start.node);
        
        successiveLinking();
        return;
        
    }

    /**
     * 
     * pre: 0<=diff<=x.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem x, int diff) 
    {    
        x.key -= diff;
        if(lazyDecreaseKeys)
        {   
            HeapNode parent = x.node.parent;
            if (parent != null && x.key < parent.item.key){
                cut(x.node, parent);
                if (!lazyMelds) {
                    if (x.key < min.key){
                        min = x;
                    }
                    successiveLinking();
                }
                cascadingCut(parent);

            }
            
        }else {
            heapify(x);
        }

        if (x.key < min.key){
            min = x;
        }
        return;
    }

    private void cut(HeapNode x, HeapNode y) {
        // הסרת x מרשימת הילדים של y
        if (x.next == x) {
            y.child = null;
        } else {
            x.next.prev = x.prev;
            x.prev.next = x.next;
            if (y.child == x) {
                y.child = x.next;
            }
        }
        y.rank--;

        // הוספת x לרשימת השורשים
        x.parent = null;
        if (x.marked){
            x.marked = false;
            markedNodes --;
        }
        
        
        min.node.next.prev = x;
        x.next = min.node.next;
        min.node.next = x;
        x.prev = min.node;
        
        cuts++;
        numOfTrees++;
    }

    private void cascadingCut(HeapNode y) {
        HeapNode z = y.parent;
        if (z != null) {
            if (!y.marked) {
                y.marked = true;
                markedNodes++;
            } else {
                cut(y, z);
                 if (!lazyMelds) {
                    successiveLinking();
                }
                cascadingCut(z);
            }
        }
    }
    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapItem x) 
    {    
        decreaseKey(x, x.key+1); // make x key -1 so it is the min. 
        deleteMin();
        return; 
    }


    public void heapify(HeapItem item)
    {
        
        while (item.node.parent != null && item.key < item.node.parent.item.key) {
            item.SwapNode(item.node.parent.item);
            heapify++;
        }
    }
    private void link(HeapNode y, HeapNode x) {
        y.parent = x;
        if (x.child == null) {
            x.child = y;
            y.next = y;
            y.prev = y;
        } else {
            concatenate(x.child, y);
        }
        x.rank++;
        links++;
    }
 
    public void successiveLinking()
    {
        if (min == null) {
            return;
        }

        int maxDegree = (size > 0) ? (int) (Math.floor(Math.log(size) / Math.log(2))) + 1 : 1;
        HeapNode[] arr = new HeapNode[maxDegree*2];
        boolean done = true;
        HeapNode last = min.node.prev;
        HeapNode c = min.node;
        
        while (done) {
            if (c == last) {
                done = false;
            }
            HeapNode next = c.next;

            c.next = c;
            c.prev = c;
            HeapNode x = c;
            int d = x.rank;

            // תהליך האיחוד (Linking)
            while (arr[d] != null) {
                HeapNode y = arr[d];
                if (x.item.key > y.item.key) {
                    HeapNode temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x); // y הופך לבן של x
                arr[d] = null;
                d++;
            }

            arr[d] = x;
            c = next;
        }


        min = null;
        numOfTrees = 0;
        for (HeapNode node : arr) {
            if (node != null) {
                if (min == null) {
                    min = node.item;
                    node.next = node;
                    node.prev = node;
                } else {
                    // שימוש ב-concatenate המקורי שלך לחיבור צמתים בודדים
                    concatenate(min.node, node);
                    if (node.item.key < min.key) {
                        min = node.item;
                    }
                }
                numOfTrees++;
            }
        }

    }

    private void concatenate(HeapNode n1, HeapNode n2) {
        HeapNode n1Next = n1.next;
        HeapNode n2Prev = n2.prev;

        n1.next = n2;
        n2.prev = n1;
        n1Next.prev = n2Prev;
        n2Prev.next = n1Next;
    }

    /**
     * 
     * Meld the heap with heap2
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
        if (heap2.min == null){
            return;
        }

        if(min == null)
        {
            min = heap2.min;
            
            heapify = heap2.heapify;
            cuts = heap2.cuts;
            links = heap2.links;
            markedNodes = heap2.markedNodes;
            numOfTrees = heap2.numOfTrees;
            size = heap2.size;
            return;
        }

        heapify += heap2.heapify;
        cuts += heap2.cuts;
        links += heap2.links;
        markedNodes += heap2.markedNodes;
        numOfTrees += heap2.numOfTrees;
        size += heap2.size;

        concatenate(min.node,heap2.min.node);
        
        if (heap2.min.key < this.min.key)
        {
            this.min = heap2.min;
        }

        if (!lazyMelds) {
            successiveLinking();           
        }

    }
    
    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size()
    {
        return size; 
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return numOfTrees; 
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return markedNodes; 
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return links; 
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return cuts; 
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return heapify; 
    }
    
    /**
     * פונקציית עזר להדפסת מבנה הערימה.
     * עוברת על כל השורשים ומדפיסה את העצים שתחתיהם.
     */
    public void printHeap() {
        System.out.println("--- Heap Structure ---");
        if (min == null) {
            System.out.println("Empty Heap");
            return;
        }

        HeapNode curr = min.node;
        int treeCount = 0;
        do {
            System.out.println("Tree " + (++treeCount) + ":");
            printNode(curr, "  ");
            curr = curr.next;
            // הגנה מפני לולאה אינסופית אם הרשימה נשברה
            if (treeCount > numOfTrees + 10) {
                System.out.println("!!! Error: Potential infinite loop in root list !!!");
                break;
            }
        } while (curr != min.node && curr != null);
        System.out.println("-----------------------");
    }

    private void printNode(HeapNode node, String indent) {
        if (node == null) return;

        System.out.print(indent + "Key: " + node.item.key);
        if (node.marked) System.out.print(" (M)"); // סימון אם הצומת מסומן
        System.out.println(" [Rank: " + node.rank + "]");

        if (node.child != null) {
            HeapNode currChild = node.child;
            int childSafetyCounter = 0;
            do {
                // אם הבן מצביע על האבא בתור הבן שלו - כאן תראה את הבאג
                if (currChild == node) {
                    System.out.println(indent + "  !!! ERROR: Node is its own child !!!");
                    return;
                }
                printNode(currChild, indent + "    ");
                currChild = currChild.next;
                childSafetyCounter++;
                if (childSafetyCounter > node.rank + 5) {
                    System.out.println(indent + "  !!! Error: Potential infinite loop in children !!!");
                    break;
                }
            } while (currChild != node.child && currChild != null);
        }
}
    /**
     * Class implementing a node in a Heap.
     *  
     */
    public static class HeapNode{
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public boolean marked;
        public int rank;

        public HeapNode(HeapItem item) {
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.marked = false;
            this.rank = 0;
            this.item = item;
        }

        public HeapNode(int key, String info) {
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.marked = false;
            this.rank = 0;
            this.item = new HeapItem(this, key, info);
        }

        public Void SwapItem(HeapNode other) {
            HeapItem tempInfo = this.item;
            this.item = other.item;
            other.item = tempInfo;

            this.item.node = this;
            other.item.node = other;
            return null;
        }

        public Void MakeSon(HeapNode other){
            if (other.parent != null) {
                throw new RuntimeException("itay make bad code");
            }
            if (other == this) {
                throw new RuntimeException("how they are the same");
            }
            if (this.item.key > other.item.key){
               throw new RuntimeException("dont make bad staff");
            }
            
            HeapNode temp = other.prev;
            other.next.prev = other.prev;
            temp.next = other.next;

            if (this.child != null) {

                other.next = child;
                other.prev = child.prev;
                child.prev.next = other;
                child.prev = other; 
            }
            else{
                other.next = other;
                other.prev = other;
            }
            
            this.child = other;
            other.parent = this;
            this.rank ++;

            
            return null;
        }
    }
    
    /**
     * Class implementing an item in a Heap.
     *  
     */
    public static class HeapItem{
        public HeapNode node;
        public int key;
        public String info;

        public HeapItem(HeapNode node, int key, String info) {
            this.node = node;
            this.key = key;
            this.info = info;

            node.item = this;
        }

        public HeapItem(int key, String info) {
            this.node = new HeapNode(this);
            this.key = key;
            this.info = info;
        }


        public Void SwapNode(HeapItem other) {
            HeapNode tempInfo = this.node;
            this.node = other.node;
            other.node = tempInfo;

            this.node.item = this;
            other.node.item = other;
            return null;
        }

    }
}
