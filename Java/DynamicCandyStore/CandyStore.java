/**
 * CandyStore.java
 * This programming recitation is used to demonstrate the fundamentals of 
 * dynamic programming as we try to get the biggest bang for our buck at the
 * candy store.
 * Given the inventory at the candy store and some amount of money,
 * compute the maximum amount of calories (amongst all the items in stock)
 * that can be purchased. The inventory tells you the price and calories
 * of each item. Assume that there is unlimited stock of each item. Only whole
 * pieces of candy can be purchased.
 * The owner of the candy shop often favors making change with the minimum
 * number of coins necessary. Given a list of denominations of coins,
 * figure out the minimum number of coins used to create "n" cents.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan Velez
 */
public class CandyStore
{
    /* Subclass used to represent a candy item */
    private static class Candy
    {
        private final int calories;
        private final int price; // in cents, ie: 1.00 -> 100

        public Candy(int c, int p)
        {
            calories = c;
            price = p;
        }

        int getCalories()
        {
            return calories;
        }

        int getPrice()
        {
            return price;
        }
    }

    /**
     * This is the dynamic programming solution for buying the max 
     * amount of calories given some amount of money that can be spent.
     * @param m amount of money (in cents) that can be spent.
     * @param c array of Candy objects that can be purchased.
     * @return the maximum number of calories that can be obtained by
     * purchasing candy.
     */
    protected static int BuyCandy(int m, Candy[] c)
    {
        int max;
        int[] amount;

        if(m == 0 || c == null)
        {
            return 0;
        }

        // init memoization
        amount = new int[m+1];
        amount[0] = 0;

        for(int thisAmount = 1; thisAmount <= m; thisAmount++)
        {
            max = 0;

            for (Candy candy : c)
            {
                if (thisAmount - candy.getPrice() >= 0)
                {
                    int thisCandy = amount[thisAmount - candy.getPrice()] + candy.getCalories();
                    max = (thisCandy > max)? thisCandy : max;
                }
            }

            amount[thisAmount] = max;
            // if another candy could not be purchased with this amount of money
            if(amount[thisAmount] == 0)
            {
                // carry over the last optimal solution
                amount[thisAmount] = amount[thisAmount-1];
            }
        }

        return amount[m];
    }

    /**
     * This function, given a file of cases, prints the maximum amount of 
     * calories obtained for some amount of money and some inventory of 
     * candy. 
     * @param f the name of the input file used to test the BuyCandy()
     * method. The input file contains information for setting the inventory
     * There will be multiple test cases in the input. Each test case will
     * begin with a line with an integer n (1<=n<=5000), and an amount of
     * money m (0.01<=m<=100.00), separated by a single space, where n is
     * the number of different types of candy for sale, and m is the amount
     * of money available to spend. The monetary amount m will be expressed
     * in dollars with exactly two decimal places, and with no leading zeros
     * unless the amount is less than one dollar. There will be no dollar
     * sign. Each of the next n lines will have an integer c (1<=c<=5000)
     * and an amount of money p (0.01<=p<=100.00), separated by a single
     * space, where c is the number of calories in a single piece of candy,
     * and p is the price of a single piece of candy, in dollars and in the
     * same format as m. The input will end with a line containing "0 0.00".
     */
    public static void browseInventoryBuyCandy(String f)
    {
        File file;
        Scanner input = null;

        file = new File(f);
        try
        {
            input = new Scanner(file);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(CandyStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        int types_of_candy, money, calories, price;
        Candy candy[];

        while(input != null && input.hasNext())
        {
            types_of_candy = input.nextInt();
            candy = new Candy[types_of_candy];

            money = (int)(input.nextDouble()*100); // convert dollars to cents

            // processing input complete if these fields null
            if(types_of_candy == 0 && money == 0)
            {
                break;
            }

            for(int i = 0; i < candy.length; i++)
            {
                calories = input.nextInt();
                price = (int)(input.nextDouble()*100); // convert dollars to cents
                candy[i] = new Candy(calories, price);
            }

            System.out.println( CandyStore.BuyCandy(money, candy) );
        }
    }

    /**
     * This is the dynamic programming solution for determining the
     * minimum number of coins needed to make some amount of change
     * given some set of coin denominations.
     * @param d, an array containing the coin denominations
     * @param n, the amount of change needed in cents
     * @return the minimum number of coins needed to make change
     *         (returns positive infinity if unable to make change)
     */
    public static int minCoins(int[] d, int n)
    {
        int min;
        int[] amount;

        if(n == 0)
        {
            return n;
        }

        amount = new int[n+1];
        amount[0] = 0;

        for(int thisAmount = 1; thisAmount <= n; thisAmount++)
        {
            min = (int)Double.POSITIVE_INFINITY;

            for(int coin = 0; coin < d.length; coin++)
            {
                if((thisAmount - d[coin] >= 0) && (amount[thisAmount - d[coin]] < min))
                {
                    min = amount[thisAmount - d[coin]] + 1;
                }
            }

            amount[thisAmount] = min;
        }

        return amount[n];
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        // test BuyCandy()
        CandyStore.browseInventoryBuyCandy("input.txt");

        // test minCoins() for 0 to 20 cents
        int[] d = {2, 5, 7};
        for(int i = 0; i <= 20; i++)
        {
            System.out.println( CandyStore.minCoins(d, i) );
        }
    }
}
