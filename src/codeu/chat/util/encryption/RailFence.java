package codeu.chat.util.encryption;

/**
 * The rail fence cipher is a cipher that uses transposition rather than
 * substitution, meaning that it changes the order of letters. This algorithm
 * works by first writing the letters on a “fence” going up and down on the
 * rails with each successive letter.
 * <p>
 * Help from the internet obtained.
 *
 * @author Kuan Chi Chen
 */
public class RailFence implements EncryptionStrategy {
    /**
     * Number of fence rails, that is, the key of the algorithm
     **/
    private static final int KEY = 3;

    /**
     * Index of the input string
     **/
    private int textIndex;

    /**
     * Rows and columns of the 2D rail array
     **/
    private int row = 0;
    private int col = 0;

    /**
     * 2D char array that represents the rail fence
     **/
    private char rail[][];

    /**
     * Boolean that checks if the direction of insertion is downwards
     **/
    private boolean isDown;

    /**
     * Final output
     **/
    private String result;

    /**
     * Perform encryption
     *
     * @param plainText
     * @return cipherText
     * @see EncryptionStrategy#encrypt(java.lang.String)
     */
    @Override
    public String encrypt(String plainText) {

        isDown = false;
        row = 0;
        col = 0;

        rail = new char[KEY][plainText.length()];

        for (int i = 0; i < plainText.length(); i++) {
            // check the direction of insertion
            isDown = setInsertDown(KEY, isDown, row);
            // fill the places that are supposed to hold the current char
            rail[row][col++] = plainText.charAt(i);
            // determine whether to go up or down on the rails
            row = updateRow(isDown, row);
        }

        // read the rails left to right to produce the cipher text
        result = "";
        for (int i = 0; i < KEY; i++) {
            for (int j = 0; j < plainText.length(); j++) {
                if (rail[i][j] != '\0') {
                    result += rail[i][j];
                }
            }
        }

        return result;
    }

    /**
     * First locate the spots where chars should be put with '*', then fill the
     * 2D array representing the rail with input, finally reproduce the original
     * string by reading up and down the rails
     *
     * @param cipherText
     * @return plainText
     * @see EncryptionStrategy#decrypt(java.lang.String)
     */
    @Override
    public String decrypt(String cipherText) {

        isDown = false;
        row = 0;
        col = 0;

        rail = new char[KEY][cipherText.length()];

        for (int i = 0; i < cipherText.length(); i++) {
            // check the direction of insertion
            isDown = setInsertDown(KEY, isDown, row);
            // mark the places that are supposed to hold chars with '*'
            rail[row][col++] = '*';
            // determine whether to go up or down on the rails
            row = updateRow(isDown, row);
        }

        textIndex = 0;
        // fill the spots identified with input text
        for (int i = 0; i < KEY; i++) {
            for (int j = 0; j < cipherText.length(); j++) {
                if (rail[i][j] == '*' && textIndex < cipherText.length()) {
                    rail[i][j] = cipherText.charAt(textIndex++);
                }
            }
        }

        // read the rails up and down to reproduce the original text
        result = "";
        row = 0;
        col = 0;
        for (int i = 0; i < cipherText.length(); i++) {
            isDown = setInsertDown(KEY, isDown, row);
            if (rail[row][col] != '*') {
                result += rail[row][col++];
            }
            row = updateRow(isDown, row);
        }

        return result;
    }

    /**
     * Helper method for decrypt in rail fence, determines whether to go up or
     * down a rail
     *
     * @param isDown
     * @param row
     * @return the updated row number
     */
    private int updateRow(boolean isDown, int row) {
        if (isDown) {
            row++;
        } else {
            row--;
        }
        return row;
    }

    /**
     * Helper method for decrypt in rail fence, see if we should go up or down a
     * rail by checking the if we reached the top or end of the rails
     *
     * @param key
     * @param isDown
     * @param row
     * @return the updated isDown
     */
    private boolean setInsertDown(int key, boolean isDown, int row) {
        if (row == 0) {
            isDown = true;
        }
        if (row == key - 1) {
            isDown = false;
        }
        return isDown;
    }

}
