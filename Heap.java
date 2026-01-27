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


        return; // should be replaced by student code
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
        return; // should be replaced by student code
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapItem x) 
    {    
        return; // should be replaced by student code
    }


    public void heapify(HeapItem item)
    {
        
        while (item.node.parent != null && item.key > item.node.parent.item.key) {
            item.SwapNode(item.node.parent.item);
        }
    }
 
    public void successiveLinking()
    {
        if (min == null) {
            return;
        }

        int maxDegree = (size > 0) ? (int) (Math.floor(Math.log(size) / Math.log(2))) + 1 : 1;
        HeapNode[] arr = new HeapNode[maxDegree*2];
        boolean notfirst = false;
        HeapNode c = min.node;
        while (notfirst && c != min.node) {
            notfirst = true;

            while (arr[c.rank] != null) {
                heapify ++;
                c.MakeSon(arr[c.rank]);
                links ++;
                numOfTrees --;
                arr[c.rank-1] = null;
            }

            arr[c.rank] = c;
            c = c.next;
        }

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

        heap2.min. = 
        min


    }
    
    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size()
    {
        return size; // should be replaced by student code
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return numOfTrees; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return markedNodes; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return links; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return cuts; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return heapify; // should be replaced by student code
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

            if (this.item.key > other.item.key){
                this.SwapItem(other);
            }
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

        public HeapItem() {
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
