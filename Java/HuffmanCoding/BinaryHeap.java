/**
 * BinaryHeap.java
 * 
 * This class is used to implement the priority queue necessary to build
 * a HuffmanTree. The HuffmanTree must implement Comparable so that it may
 * be processed by the BinaryHeap.
 */
package huffman;

import java.util.Arrays;

public class BinaryHeap<T extends Comparable<T>>
{
    private static final int TOP = 0;
    private static final int DEFAULT_CAPACITY = 256;
    T[] heap;
    private int size;


    @SuppressWarnings("unchecked")
    /**
     * BinaryHeap() : constructs a new BinaryHeap. Java does not allow the
     * construction of arrays of generic types, so the work-around
     * solution in this implementation is to extend T from Comparable <T>,
     * and then created a new array of Comparable objects casterd over as
     * the parameterized type.
     */
    public BinaryHeap()
    {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }


    /**
     * add(value) : add an item to the bottom of the heap and re-organize
     * the heap by percolating items upward.
     * @param value : the item to be added to the heap.
     */
    public void add(T value)
    {
        if(this.size >= heap.length - 1)
        {
            heap = this.resize();
        }

        heap[size++] = value;

        PercUp();
    }

    /**
     * remove() : method to remove the top item of the heap and
     * re-organize the heap (by placing the last item at the top and
     * percolating items downward).
     * @return the top item of the heap (the generic type must be
     * casted as the appropriate reference type at method call.
     */
    public T remove()
    {
        T result;

        if(this.size < 1)
        {
            System.out.println("WARNING: heap is empty - null return!");
            return null;
        }

        result = heap[TOP];

        heap[TOP] = heap[--size];
        heap[size+1] = null;

        PercDown();

        return result;
    }

    /** HELPER FUNCTIONS */

    protected void PercUp()
    {
        int index = this.size-1;

        while( hasParent(index) && parent(index).compareTo(heap[index]) > 0)
        {
            swap(index, parentIndex(index));
            index = parentIndex(index);
        }
    }

    protected void PercDown()
    {
        int index = TOP;

        while (hasLeftChild(index))
        {
            int smallerChild = leftIndex(index);

            // check to see if the right is smaller than the left
            if( hasRightChild(index) &&
                    heap[leftIndex(index)].compareTo(heap[rightIndex(index)]) > 0)
            {
                smallerChild = rightIndex(index);
            }

            if (heap[index].compareTo(heap[smallerChild]) > 0)
            {
                swap(index, smallerChild);
            }

            else
            {
                break;
            }

            index = smallerChild;
        }
    }

    protected void swap(int a, int b)
    {
        T temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    protected T[] resize()
    {
        return Arrays.copyOf(heap, heap.length * 2);
    }

    protected boolean hasParent(int index)
    {
        return index > 0;
    }

    protected T parent(int i)
    {
        return heap[parentIndex(i)];
    }

    protected int parentIndex(int i)
    {
        return (i-1) / 2;
    }

    protected boolean hasLeftChild(int i)
    {
        return leftIndex(i) < size;
    }

    protected boolean hasRightChild(int i)
    {
        return rightIndex(i) < size;
    }

    protected int leftIndex(int i)
    {
        return (i * 2) + 1;
    }

    protected int rightIndex(int i)
    {
        return (i * 2) + 2;
    }

    protected int getSize()
    {
        return this.size;
    }
    
}
