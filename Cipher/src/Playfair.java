import java.util.*;
/**
 *
 * @author Dan Boxall
 */
public class Playfair
{
    private char[][] keyTable;
    private Map<Character, int[]> keyTableCoords;
    private List<char[]> digraphs;
    private String msg;
    private String key;
    
    public Playfair(String msg, String key)
    {
        this.keyTable = new char[5][5];
        this.keyTableCoords = new HashMap<>();
        this.digraphs = new ArrayList<>();
        this.msg = msg.replaceAll("J", "I").replaceAll(" ", "");
        this.key = key.replaceAll("J", "I").replaceAll(" ", "");
    }
    
    /**
     * Prints out the key table as a 5x5 grid, used for testing
     */
    public void printKeyTable()
    {
        for (char[] line : this.keyTable)
        {
            for (char c : line)
            {
                System.out.println(c + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * @return alphabet with all the letters of the key at the beginning
     * as a string, and no repeating letters
     */
    private String getAlphaForKeyTable()
    {
        StringBuilder alphabet = new StringBuilder();
        char[] actualAlphabet = (this.key + "ABCDEFGHIKLMNOPQRSTUVWXYZ").toCharArray();
        for (char c : actualAlphabet)
        {
            // if the letter is not already in the alphabet
            if (alphabet.toString().indexOf(c) == -1)
            {
                alphabet.append(c);
            }
        }
        return alphabet.toString();
    }
    
    /**
     * Populates the 5x5 key table and also fills the map
     * with the key table coords
     */
    private void generateKeyTable()
    {
        String keyAlphabet = this.getAlphaForKeyTable();
        int alphaIndex = 0;
        for (int row = 0; row < 5; row++)
        {
            for (int col = 0; col < 5; col++)
            {
                char letter = keyAlphabet.charAt(alphaIndex);
                this.keyTable[row][col] = letter;
                int[] coord = {row, col};
                this.keyTableCoords.put(letter, coord);
                alphaIndex++;
            }
        }
    }
    
    /**
     * Splits entire message into digraphs, ie pairs of letters. Any time
     * a letter is repeated in a pair, the second letter is replaced with 'X'
     */
    private void createDigraphs()
    {
        StringBuilder msg = new StringBuilder(this.msg);
        char[] digraph;
        while (msg.length() > 0)
        {
            digraph = new char[2];
            digraph[0] = msg.charAt(0);
            msg.deleteCharAt(0);
            if (msg.length() == 0 || msg.charAt(0) == digraph[0])
            {
                digraph[1] = 'X';
            }
            else
            {
                digraph[1] = msg.charAt(0);
                msg.deleteCharAt(0);
            }
            this.digraphs.add(digraph);
        }
    }
    
    /**
     * Encodes digraph based on one of three different rules depending on their positions
     * in the keyTable
     * @param digraph char array of two letters
     * @param shift either 1 or -1 depending on whether encoding or decoding
     * @return a pair of encoded letters, as a String
     */
    private String encodeDigraph(char[] digraph, int shift)
    {
        StringBuilder encodedText = new StringBuilder();
        int[] firstCoord = this.keyTableCoords.get(digraph[0]).clone();
        int[] secondCoord = this.keyTableCoords.get(digraph[1]).clone();
        
        // if each of the letters are in the same row then change each
        // letter to the letter immediately to the right, or if it's at 
        // the end of the row, to 0
        if (firstCoord[0] == secondCoord[0])
        {
            // first letter
            if (firstCoord[1] == 4 && shift == 1)
                firstCoord[1] = 0;
            else if (firstCoord[1] == 0 && shift == -1)
                firstCoord[1] = 4;
            else
                firstCoord[1] += shift;   
            // second letter
            if (secondCoord[1] == 4 && shift == 1)
                secondCoord[1] = 0;
            else if (secondCoord[1] == 0 && shift == -1)
                secondCoord[1] = 4;
            else
                secondCoord[1] += shift;
        }
        
        // if the letters are in the same column as each other then change
        // each letter to the letter immmediately below (or above if decoding)
        else if (firstCoord[1] == secondCoord[1])
        {
            // first letter
            if (firstCoord[0] == 4 && shift == 1)
                firstCoord[0] = 0;
            else if (firstCoord[0] == 0 && shift == -1)
                firstCoord[0] = 4;
            else
                firstCoord[0] += shift;
            // second letter
            if (secondCoord[0] == 4 && shift == 1)
                secondCoord[0] = 0;
            else if (secondCoord[0] == 0 && shift == -1)
                secondCoord[0] = 4;
            else
                secondCoord[0] += shift;
         }
        
        // if the letters are in diffent row and column, they make a "box"
        // in the keyTable. Replace each letter in the other corner on the same
        // row of the box
        else
        {
            int temp = firstCoord[1];
            firstCoord[1] = secondCoord[1];
            secondCoord[1] = temp;
        }
        encodedText.append(this.keyTable[firstCoord[0]][firstCoord[1]]);
        encodedText.append(this.keyTable[secondCoord[0]][secondCoord[1]]);
        return encodedText.toString();
    }
    
    /**
     * Splits the encoded msg into groups of five letters, seperated by spaces
     * @param msg the encoded message
     * @return String
     */
    private String splitString(String msg)
    {
        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < msg.length(); i++)
        {
            newStr.append(msg.charAt(i));
            if ((i + 1) % 5 == 0)
            {
                newStr.append(" ");
            }
        }
        return newStr.toString();
    }
    
    /**
     * Splits the message into digraphs (pairs of letters), generates key table
     * and encodes each digraph
     * @return entire message encoded as a String
     */
    public String encodePlayfair()
    {
        this.generateKeyTable();
        this.createDigraphs();
        StringBuilder encodedString = new StringBuilder();
        int counter = 0;
        for (char[] digraph : this.digraphs)
        {
            encodedString.append(this.encodeDigraph(digraph, 1));
        }
        return this.splitString(encodedString.toString());
    }
    
    public String decodePlayfair()
    {
        this.generateKeyTable();
        StringBuilder decodedStr = new StringBuilder();
        int index = 0;
        String message = this.msg;
        while (index < message.length())
        {
            char[] digraph = new char[2];
            //try {
            digraph[0] = message.charAt(index++);
            digraph[1] = message.charAt(index++);
            decodedStr.append(this.encodeDigraph(digraph, -1));
            //} catch (Exception e)
            //{ System.out.println(index + " hello" + message.length());
            //break;
            //}
            //System.out.println(decodedStr);
        }
        return this.splitString(decodedStr.toString());
    }
}
