/**
 * Huffman.java
 * 
 * A Huffman code is a type of optimal prefix code that is commonly used
 * for lossless data compression. This programming recitation uses the  
 * frequencies of each byte in a file to create Huffman codes for each of the 
 * bytes. The frequency array will be of size 256, such that in index i, the
 * frequency of the byte with value (i - 128) from the specified file will
 * be stored. (This system has been selected because the value of a byte
 * ranges from -128 to 127 but the array index must start at 0).
 * A Huffman tree that omits unused symbols produces the most optimal code
 * lengths.
 * <p>
 * This technique works by creating a binary tree in which a node can
 * either by a lead node or an internal node. All nodes start as 
 * leaf nodes, which contain the symbol itself and its weight. Internal
 * nodes are represented by the symbol "-1" and track only the sum of the
 * weights of its subtrees. As a common convention, bit '0' represents
 * following the left child and bit '1' represents following the right
 * child.
 * 
 * The process essentially begins with the leaf nodes containing the
 * weights of the symbol they represent, then a new internal node whose
 * children are the 2 nodes with smallest probability is created, such
 * that the new node's probability is equal to the sum of the children's
 * probability. With the previous 2 nodes merged into one node (thus not
 * considering them anymore), and with the new node being now considered,
 * the procedure is repeated until one node remains, the Huffman tree.
 * The simplest construction algorithm uses a priority queue where the
 * node with lowest probability is given highest priority.
 *
 * Dependencies: HuffmanTree.java, BinaryHeap.java
 */

package huffman;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Huffman
{
    // the default capacity is the size of extended ASCII, i.e. 256
    private static final int DEFAULT_CAPACITY = 256;
    // store the frequency of character occurrences when reading in from file
    private static int[] Freq;
    // store the HuffmanTree generated to create Huffman codes
    private static HuffmanTree Tree;
    // store the Huffman Codes
    private static boolean HuffmanCode[][];
    private static BinaryHeap PriorityQ;
    // buffer to write codes as the tree is traversed
    private static boolean Buffer[];
    private static int buffIndex, buffSize;

    /**
     * makeHuffmanCodes(freq[]) : generates Huffman codes based on the
     * frequency distribution of the occurrence of characters in a file.
     * @param freq an array representing the frequency distribution of the
     * occurrence of characters in a file.
     * @return the Huffman code table generated according to the frequency
     * distribution.
     */
    public static boolean[][] makeHuffmanCodes(int[] freq)
    {
        // initialize the priority queue
        PriorityQ = new BinaryHeap<>();
        // initialize tje Huffman code table
        HuffmanCode = new boolean[DEFAULT_CAPACITY][];

        if(freq == null)
        {
            System.out.println(
                "WARNING: attempting to pass null argument to makeHuffmanCodes(int[])");
            return null;
        }

        // load each node into the priority queue
        for(int index = 0; index < DEFAULT_CAPACITY; index++)
        {
            // skip symbols with 0 weight
            if(freq[index] == 0)
            {
                continue;
            }

            PriorityQ.add( new HuffmanTree(index, freq[index]) );
        }

        // generate the HuffmanTree
        while(PriorityQ.getSize() > 1)
        {
            HuffmanTree node1 = (HuffmanTree) PriorityQ.remove();
            HuffmanTree node2 = (HuffmanTree) PriorityQ.remove();

            HuffmanTree nodeInternal = new HuffmanTree(
                    HuffmanTree.internal_node,
                    node1.getFreq() + node2.getFreq()
            );
            nodeInternal.setLeft(node1);
            nodeInternal.setRight(node2);

            PriorityQ.add(nodeInternal);
        }

        // remove the HuffmanTree from the priority queue
        Tree = (HuffmanTree) PriorityQ.remove();

        // initialize the buffer used for writing the Huffman codes
        Buffer = new boolean[DEFAULT_CAPACITY];
        buffIndex = 0;
        buffSize = 0;

        // write the codes from the HuffmanTree to the table
        writeCode(Tree);

        return HuffmanCode;
    }

    /**
     * writeCode(node) : method that populates the HuffmanCode table for
     * each symbol with a frequency count greater than 0 by recursively
     * traversing each left subtree and right subtree.
     * (Note: left node -> 0 -> false; right node -> 1 -> true).
     * @param node the root of a HuffmanTree.
     */
    private static void writeCode(HuffmanTree node)
    {
        // if this is a leaf
        if(node.getSymbol() != HuffmanTree.internal_node)
        {
            // copy its path to the Huffman code table
            HuffmanCode[node.getSymbol()] = Arrays.copyOf(Buffer, buffSize);
        }

        // go left
        if(node.hasLeft())
        {
            Buffer[buffIndex] = false;
            buffIndex++;
            buffSize++;
            writeCode(node.getLeft());
            --buffIndex;
            --buffSize;
        }

        // go right
        if(node.hasRight())
        {
            Buffer[buffIndex] = true;
            buffIndex++;
            buffSize++;
            writeCode(node.getRight());
            --buffIndex;
            --buffSize;
        }

    }

    /**
     * readfile(path, encoding) : method that reads a file and counts the
     * frequency of the occurence of each byte in that file.
     * @param path the String representation of the path to the input
     * file (e.g., "filename.txt").
     * @param encoding the encoding system used by the file to represent
     * characters (e.g., StandardCharsets.US_ASCII).
     */
    private static void readFile(String path, Charset encoding)
    {
        // stores contents of the entire file
        byte[] encoded = null;
        // stores the string representation of the file's contents
        String temp;
        // stores the char[] representation of the file's contents
        char input[];

        // read file
        try
        {
            encoded = Files.readAllBytes(Paths.get(path));
        }
        catch(IOException ex)
        {
            System.out.println("WARNING: null file pointer in readFile()");
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }

        // convert to String
        temp = new String(encoded, encoding);
        // convert to char[]
        input = temp.toCharArray();

        // init frequency table
        Freq = new int[DEFAULT_CAPACITY];
        // count the frequency of the occurence of each symbol
        for(int index = 0; index < input.length; index++)
        {
            Freq[(int)input[index]]++;
        }

    }

    /**
     * set of Huffman codes generated from some input text-file.
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        readFile("input.txt", StandardCharsets.US_ASCII);
        boolean hc[][] = makeHuffmanCodes(Freq);

        // print Huffman Codes
        for(int i = 0; i < hc.length; i++)
        {
            System.out.print("hc("+i+"): ");
            if(hc[i] == null)
            {
                System.out.println("0");
                continue;
            }


            for(int j = 0; j < hc[i].length; j++)
            {
                System.out.print(hc[i][j]+" ");
            }

            System.out.println();
        }
    }

}
