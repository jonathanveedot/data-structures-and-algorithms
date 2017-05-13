/**
 * LCS.java
 * 
 * This programming recitation is designed to demonstrate the Lowest Common 
 * Subsequence (LCS) problem.
 * 
 * The LCS problem is a common problem within computer science that requires 
 * finding the longest subsequence common to all sequences in a set of two 
 * (or more) sequences. Dynamic Programming provides an efficient approach for 
 * computing the LCS.
 */
 
public class LCS
{
    static Node[][] memo;
    static StringBuffer buffer;
    static int bufferCount;

    /**
     * lcs(first[], second[]): method that computes the longest subsequence common
     * to any two sequences.
     * @param first the first sequence of Strings for comparison
     * @param second the second sequence of Strings for comparison
     */
    static void lcs(String[] first, String[] second)
    {
        /* initialize memoization table */
        memo = new Node[first.length+1][second.length+1];
        for(int row = 0; row <= first.length; row++)
        {
            for(int col = 0; col <= second.length; col++)
            {
                memo[row][col] = new Node();
            }
        }

        /* compute the longest common subsequence */
        // the sequence elements are defined to start at 1,
        // so that is it known the LCS is empty when a subscript is zero.
        for(int row = 1; row <= first.length; row++)
        {
            for(int col = 1; col <= second.length; col++)
            {
                // if the two current subsequences are not equal
                // then retain the longer of the two sequences.
                // NOTE: this implementation is greedy and does not account for
                // both being the same length, but not identical.
                if(!first[row-1].equals(second[col-1]))
                {
                    if(memo[row][col-1].getValue() > memo[row-1][col].getValue())
                    {
                         memo[row][col].setValue( memo[row][col-1].getValue() );

                    }
                    else
                    {
                        memo[row][col].setValue( memo[row-1][col].getValue() );
                    }
                }
                // otherwise the next element is common to the subsequences,
                // so extend the least common subsequence.
                else
                {
                    memo[row][col].setValue( 1 + memo[row-1][col-1].getValue() );
                    memo[row][col].setBridge( memo[row-1][col-1] );
                    memo[row-1][col-1].setBridge( memo[row][col] );
                }
            }
        }

        /* print results */
        // print count
        System.out.println("LCS:");
        bufferCount = memo[first.length][second.length].getValue();
        System.out.println("Count: " + bufferCount);
        // backtrack through the memoization table to read out the LCS
        buffer = new StringBuffer();
        backtrack(first.length, second.length, bufferCount, first );
        System.out.println(buffer.toString());
    }

    /**
     * backtrack(r, c, key, list[]): method that backtracks through the memoization
     * table generated by method lcm(first, second) to read out the least common
     * subsequence.
     * @param r the number of rows in the memoization table
     * @param c the number of columns in the memoization table
     * @param key the length of the longest common subsequence
     * @param list the first of the two sequences used to compute the lcs, used
     * to print each symbol in the lcs as it is encountered.
     */
    private static void backtrack(int r, int c, int key, String[] list)
    {
        if(bufferCount == 0 || r == 0 || c == 0 || key == 0)
        {
            return;
        }

        if(memo[r][c].getBridge() != null)
        {
            buffer.append(list[r-1]).append(" ");
            --bufferCount;
            backtrack(r-1, c-1, key-1, list);
        }

        if(memo[r][c].getValue() == key)
        {
            for(int i = 1; c-i > 0; i++)
            {
                backtrack(r, c-i, key, list);
            }

            backtrack(r-1, c, key, list);
        }

        // otherwise the bounds of this partition (m[r][c] < key) are exceeded
        return;
    }

    /* node structure used to track extensions of the least common subsequence */
    private static class Node
    {
        int value;
        Node bridge;

        void setValue(int n)
        {
            value = n;
        }

        int getValue()
        {
            return value;
        }

        void setBridge(Node r)
        {
            bridge = r;
        }

        Node getBridge()
        {
            return bridge;
        }
    }

    /**
     * main(args[]): main method demonstrates implementation of LCS using sample
     * input.
     * @param args
     */
    public static void main(String args[])
    {
        String[] first = {"in", "ni", "nat", "jag", "dep", "y", "togo",
            "il", "ru", "pur", "de", "tuh", "huh", "jon", "stewart"};
        String[] second = {"nat", "in", "jag", "dep", "huh", "tuh", "il",
            "de", "tuh", "de", "huh", "jon", "y", "stewart"};

        LCS.lcs(first, second);
    }
}
