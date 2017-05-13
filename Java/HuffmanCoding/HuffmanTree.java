/**
 * HuffmanTree.java
 * 
 * each HuffmanTree object represents a node within the HuffmanTree.
 * the HuffmanTree class must implement Comparable to support
 * priority-queue processing using BinaryHeap.
 */
package huffman;

public class HuffmanTree implements Comparable<HuffmanTree>
{
    /**
     * a symbol representing an unsigned byte value from 0 to 255
     * (see extended ASCII table)
     */
    private final int symbol;

    /** the symbol of an internal node is -1
     *  i.e., the node does not represent a valid index
     */
    static final int internal_node = -1;

    /**
     * the frequency of this symbol's occurrences.
     */
    private int frequency;

    /**
     * the left and right children of this node.
     */
    private HuffmanTree left, right;

    HuffmanTree(int symbol, int frequency)
    {
        this.symbol = symbol;
        this.frequency = frequency;
    }

    @Override
    /**
     * compareTo(that) : method that compares the frequency of this node
     * to that of another node.
     * @param that : the node that this node is being compared to.
     * @return -1 if this is less than that, 1 if this is greater than
     * that, and 0 if they are otherwise equal.
     */
    public int compareTo(HuffmanTree that)
	  {
        if (this.getFreq() < that.getFreq())
            return -1;
        else if (this.getFreq() > that.getFreq())
            return 1;
        else
            return 0;
	  }

	/** GETTERS AND SETTERS */
  
    int getSymbol()
    {
        return symbol;
    }

    int getFreq()
    {
        return frequency;
    }

    void setLeft(HuffmanTree node)
    {
       this.left = node;
    }

    void setRight(HuffmanTree node)
    {
        this.right = node;
    }

    HuffmanTree getLeft()
    {
        return this.left;
    }

    HuffmanTree getRight()
    {
        return this.right;
    }

    boolean hasLeft()
    {
        return this.left != null;
    }

    boolean hasRight()
    {
        return this.right != null;
    }
    
}
