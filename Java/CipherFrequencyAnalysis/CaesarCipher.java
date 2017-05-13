/**
 * This is a programming recitation that demonstrates the fundamentals of
 * frequency analysis by decoding ciphers. A cipher is a method for
 * encoding a message by replacing each character of the message with
 * another character that is a certain distance further down the
 * alphabet, wrapping around at the end. Caesar is notorious for employing
 * this method of encoding messages, and often used a shift factor of 3,
 * but any natural number can be used. This program automatically decodes
 * ciphers with unknown shift factors by using letter frequencies to
 * determine the most probable shift factor used to encode the message.
 * 
 * In English text, some letters are used more frequently than others. By
 * analyzing a large volume of text, one can derive the table of approx
 * percentage frequencies of the 26 letters of the alphabet that is stored
 * in the class variable table[] below. This table is used to compute the
 * most probable shift factor using chi square statistics.
 */

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Jonathan Velez
 */
public class CaesarCipher
{
    protected static boolean rounding = false;
    private final static int NOT_A_LETTER = Integer.MAX_VALUE;
    // approximate percentage frequencies of the
    // twenty-six letters of the alphabet
    private static final double[] table =
    {
        8.2, 1.5, 2.8, 4.3, 12.7, 2.2, 2.0, 6.1, 7.0,
        0.2, 0.8, 4.0, 2.4, 6.7, 7.5, 1.9, 0.1, 6.0,
        6.3, 9.1, 2.8, 1.0, 2.4, 0.2, 2.0, 0.1
    };

    /** METHODS FOR ENCODING/DECODING CIPHER */

    /**
     * converts a lower-case letter in the range 'a' to 'z' into the
     * corresponding natural number in the range 0 to 25.
     * @param c some character.
     * @return the natural number representation of c
     */
    int let2nat(char c)
    {
        // if c is not a letter
        if(!Character.isAlphabetic(c))
        {
            // return positive infinity;
            return NOT_A_LETTER;
        }

        // otherwise return the natural number represenation of c
        c = Character.toLowerCase(c);
        return c - 'a';
    }

    /**
     * performs the inverse method of let2nat().
     * @param code value between 0 and 25 (inclusive)
     * @return the corresponding letter from 'a' to 'z'
     */
    char nat2let(int code)
    {
        return (char)(code + 'a');
    }

    /**
     * applies a shift factor in the range of 0 to 25 to a lower-case
     * letter in the range 'a' to 'z', wrapping around at the end of the
     * alphabet. Characters outside of the range of 'a' to 'z' should be
     * returned un-shifted.
     * @param shftAmt number of shifts (positive -> right, negative -> left).
     * @param c the character to be shifted.
     * @return the character that results from shifting c.
     */
    char shift(int shftAmt, char c)
    {
        // tracks the natural number representation of the character
        // resulting from the shift
        int newLetter;

        // if c is not a letter, ignore it
        if(!Character.isAlphabetic(c))
        {
            return c;
        }
        // else if shifting the character left and
        // wrapping to the end from 0
        else if(shftAmt < 0 && let2nat(c) + shftAmt < 0)
        {
            newLetter = 26 + (let2nat(c) + shftAmt);
        }
        // otherwise shift
        else
        {
            newLetter = (let2nat(c) + shftAmt) % 26;
        }

        return nat2let(newLetter);
    }

    /**
     * encodes a string using a given shift factor.
     * @param shftAmt value from 0 to 25 to shift each character in str.
     * @param str a String to be encoded by shifting each character.
     * @return the encoded String which has had each letter shifted.
     */
    String encode(int shftAmt, String str)
    {
        // stores the encoded string
        char[] temp;

        if(shftAmt < 1 || str.length() < 1)
        {
            return str;
        }

        // store the original string in all lowercase
        temp = str.toCharArray();

        // shift each character
        for(int i = 0; i < temp.length; i++)
        {
            if(Character.isLowerCase(temp[i]))
                temp[i] = shift(shftAmt, temp[i]);
        }

        return String.valueOf(temp);
    }

    /**
     * performs the inverse method of encode().
     * @param shftAmt value from 0 to 25 to shift each character in str.
     * @param str a String to be decoded by shifting each character.
     * @return the decoded String which has had each letter shifted.
     */
    String decode(int shftAmt, String str)
    {
        // stores the decoded string
        char[] temp;

        if(shftAmt < 1 || str.length() < 1)
        {
            return str;
        }

        // decoding requires left shift (i.e., a negative shift value)
        shftAmt = 0 - shftAmt;

        // store the original string in all lowercase
        temp = str.toCharArray();

        // shift each character
        for(int i = 0; i < temp.length; i++)
        {
            if(Character.isLowerCase(temp[i]))
                temp[i] = shift(shftAmt, temp[i]);
        }

        return String.valueOf(temp);
    }

    /** METHODS FOR FREQUENCY ANALYSIS FOR COMPUTING SHIFT FACTOR */

    /**
     * calculates the number of lower-case letters in a String.
     * @param str the String being processed.
     * @return the number of lower-case letters in str.
     */
    int lowers(String str)
    {
        int lowers = 0;
        for(int i = 0; i < str.length(); i++)
        {
            if(Character.isLowerCase(str.charAt(i)))
                lowers++;
        }
        return lowers;
    }

    /**
     * calculates the frequency of a given character in a String.
     * @param c a character
     * @param str the String being processed.
     * @return the number of occurrences of c within str.
     */
    int count(char c, String str)
    {
        int count = 0;
        for(int i = 0; i < str.length(); i++)
        {
            if( Character.isLowerCase(str.charAt(i)) && str.charAt(i) == c ) 
            {
                count++;
            }
            
        }

        return count;
    }

    /**
     * calculates the percentage of one integer with respect to another.
     * @param num1 the number of occurrences of a character within a String.
     * @param num2 the total number of characters within that String.
     * @return the percentage frequency of that character's occurrence.
     */
    double percent(int num1, int num2)
    {
        double percentage;

        percentage = ((double)num1/(double)num2)*100;

        return percentage;
    }

    /**
     * computes a list of percentage frequencies of each of the lower-case
     * letters 'a' to 'z' in a String of characters.
     * @param str the String being processed.
     * @return the list of percentage frequencies of each of the letters
     * 'a' to 'z' in a String of characters.
     */
    double[] freqs(String str)
    {
        double[] frequencies;

        // one index for each letter 'a' to 'z'
        frequencies = new double[26];

        for(int i = 0; i < lowers(str); i++)
        {
            if(let2nat(str.charAt(i)) == Integer.MAX_VALUE)
            {
                continue;
            }

            frequencies[ let2nat(str.charAt(i)) ] =
                    percent( count(str.charAt(i), str), lowers(str) );
        }

        return frequencies;
    }

    /**
     * rotates a percentage frequency list n places to the left, wrapping
     * around at the start of the list.
     * @param n the number of rotations to perform, assumed to be in the
     * range of zero to the length of the list.
     * @param list the original percentage frequency list.
     * @return the rotated percentage frequency list.
     */
    double[] rotate(int n, double[] list)
    {
        double[] new_list;
        int index;

        if(n == 0)
        {
            return list;
        }

        new_list = new double[list.length];

        index = 0;
        while(index < new_list.length)
        {
            new_list[index] = list[(index+n)%list.length];
            ++index;
        }

        return new_list;
    }

    /**
     * calculates the chi square statistic for a list of observed
     * frequencies with respect to a list of expected frequencies.
     * @param os a list of observed frequencies.
     * @return the chi square statistic.
     */
    double chisqr(double[] os)
    {
        BigDecimal rounded;

        if(os == null)
        {
            System.out.println("WARNING: empty list passed into chisqr(double[])!");
            return 0;
        }

        double chisqr = 0;
        for(int i = 0; i < os.length; i++)
        {
            chisqr += Math.pow(os[i]-table[i], 2) / table[i];
        }

        if(rounding)
        {
            rounded = new BigDecimal(chisqr).setScale(4, RoundingMode.HALF_EVEN);
            return rounded.doubleValue();
        }
        else
        {
            return chisqr;
        }

    }

    /**
     * searches for the first position (counting from zero) at which a
     * value occurs in a list, assuming that it occurs at least once.
     * @param a the value.
     * @param list the search-space.
     * @return the position of the first occurrence of the value.
     */
    int position(double a, double[] list)
    {
        for(int i = 0; i < list.length; i++)
        {
            if(list[i] == a)
            {
                return i;
            }
        }

        System.out.println("WARNING: value "+a+" not in list!");
        return Integer.MAX_VALUE;
    }

    /**
     * attempts to decode a String by first calculating its letter
     * frequencies, then calculating the chi square value of each rotation
     * (in the range 0 to 25) of this observed list with respect to the
     * table of expected frequencies. The position of the minimum value of
     * the set of chi squares is selected as the shift factor to decode
     * the original String.
     * @param str the String to be decoded.
     * @return the decoded String.
     */
    String crack(String str)
    {
        double[] freqs, current_list, chisquares;
        double min;

        freqs = freqs(str);
        chisquares = new double[26];
        min = Double.POSITIVE_INFINITY;

        for(int i = 0; i < chisquares.length; i++)
        {
            current_list = rotate(i, freqs);
            chisquares[i] = chisqr(current_list);
            if(chisquares[i] < min)
            {
                min = chisquares[i];
            }
        }

        return decode(position(min, chisquares), str);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // fix to handle spaces
        CaesarCipher Cipher = new CaesarCipher();
        System.out.println(
            Cipher.crack(
                Cipher.encode(3, "Such a convoluted cipher could cause a crisis!")
            )
        );
    }

}
