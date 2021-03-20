/**
 * DES.java - implementation of the simplified DES-Type Algorithm
 *
 * Sung Kim
 */
import java.util.ArrayList; 
import java.util.Collections;
public class DES
{
    private static int sbox1[][] = {
            {0b101, 0b010, 0b001, 0b110, 0b011, 0b100, 0b111, 0b000}, // row 0
            {0b001, 0b100, 0b110, 0b010, 0b000, 0b111, 0b101, 0b011}  // row 1 
        };
    private static int sbox2[][] = {
            {0b100, 0b000, 0b110, 0b101, 0b111, 0b001, 0b011, 0b010}, // row 0
            {0b101, 0b011, 0b000, 0b111, 0b110, 0b010, 0b001, 0b100}  // row 1
        };

    public static void main(String[] args)
    {   
        int input = 0b011100100110;
        int masterkey = 0b010011001;
        int round = 4;

        int result = des_round(input, masterkey,round);
    }

    //des_round() -- Performs a single round of simpl ified DES algorith on given input pt, 
    //   for round, using masterkey.
    public static int des_round(int pt, int masterkey, int round)
    {
        //isolate the left and right halves of the input
        //use bitwise AND operation to isolate the bits we want (upper 6 for left, lower 6 for right)
        int l_in = (pt & 0b111111000000) >> 6;   //need to right shift 6 spots so result is in lower 6 bits
        int r_in = (pt & 0b000000111111);

        int result = 0; // temporary
        String originalLeft = addZeros(Integer.toBinaryString(l_in),6); //adding leading zeros
        String originalRight = addZeros(Integer.toBinaryString(r_in),6);

        //1. expands 6-bit r-input  to 8-bits

        String expandedRight = expandBits(Integer.toBinaryString(r_in));

        //2. generates round key based on the master key and round number
        String key = currentKey(masterkey, round);

        //3a. Xoring expanded_r and roundkey to f()
        String xor = xor(key,expandedRight).toString();

        //3b. Splits result into two 4-bit sequences
        String part1 = xor.substring(0,4), part2 = xor.substring(4,8);
        //3d. Concatenates together to single 6-bit output
        String concatenate = sBoxes(part1,part2); 
        //4. XORs result of f() with original left-input. This result is the right part of output
        String right = xor(concatenate,originalLeft).toString();
        //Print out results of the round (the round input, the round key, and the round output)
        //5. Combine with original right-input (which is the left part of the output)
        String finalResult = originalRight + right;

        System.out.println("8 bit right key: " + expandedRight);
        System.out.println("New Round Key: " + key);
        System.out.println("Step 3a: XORing round key and expanded right_in: "+xor);
        System.out.println("Step 3d: After concatinating two different values of S-boxes: " + concatenate);

        System.out.println("Original left: " + originalLeft);
        System.out.println("Step 4: XOR with f(): " + right);

        System.out.println("Step 5: " + "0b"+finalResult);

        return result = Integer.parseInt(finalResult,2);
    }

    /*
     * This method expands 6 bit input to 8 bit
     */
    public static String expandBits (String right)
    {
        String newRight = "";
        char temp3, temp4;
        for (int i = 0; i < 6;i++)
        {
            if(i == 2)
            {
                temp3 = right.charAt(i);
                temp4 = right.charAt(i+1);
                newRight = newRight + temp4 + temp3 + temp4 + temp3;
                i = i + 2;
            }
            newRight = newRight + right.charAt(i);
        }            
        return newRight; 
    }

    /*
     * Creates a new key that depends on the current round
     */
    public static String currentKey  (int masterkey, int round)    
    {
        String roundKey = "";
        int i = round-1 , counter = 0;
        String key = Integer.toBinaryString(masterkey);
        if (key.length() < 9)
            key = "0" + key;
        while (counter < 8)
        {
            if(i < key.length())
            {
                roundKey = roundKey + key.charAt(i);               
            }
            if (i == key.length())
            {
                i = 0;
                roundKey = roundKey + key.charAt(i);
            }
            counter++; i++;
        }
        return roundKey;
    }

    /*
     * Since mostly I work with strings I created a method that gets each character
     * of passed string and XORs it
     */
    public static StringBuffer xor (String currentKey, String expandedRight)
    {
        StringBuffer xor = new StringBuffer("");
        for (int i = 0; i < currentKey.length(); i++)
        {
            xor.append(currentKey.charAt(i) ^ expandedRight.charAt(i));
        }        
        return xor;
    }

    /*
     * Here I get the values of S-boxes and then return the concatenated 6 bit string
     */
    public static String sBoxes (String part1, String part2)
    {
        int row1 = Integer.parseInt(part1.substring(0,1),2);
        int col1 = Integer.parseInt(part1.substring(1,4),2);
        int row2 = Integer.parseInt(part2.substring(0,1),2);
        int col2 = Integer.parseInt(part2.substring(1,4),2);

        String one = addZeros(Integer.toBinaryString(sbox1[row1][col1]),3);
        String two = addZeros(Integer.toBinaryString(sbox2[row2][col2]),3);

        return one+two;
    }

    public static String addZeros(String bit, int range)
    {
        String str = bit;
        for (int i = str.length(); i < range; i++)
            str = "0" + str;
        return str;
    }
}
