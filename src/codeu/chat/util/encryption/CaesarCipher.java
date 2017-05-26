package codeu.chat.util.encryption;

/**
 * A Caesar cipher shifts each letter a fixed amount, in this case by 3 letters.
 *
 * @author Kuan Chi Chen
 */
public class CaesarCipher implements EncryptionStrategy {
    /**
     * Amount to shift each letter
     **/
    private static final int SHIFT = 3;

    /**
     * Shift each char in a string and mod the number of characters (256) so
     * that the resulting chars don't go out of bounds
     *
     * @param str   input
     * @param shift the amount to shift the chars by
     * @return a shifted string
     */
    protected String code(String str, int shift) {
        char[] result = str.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] = (char) ((result[i] + shift) % 256);
        }
        return new String(result);
    }

    /**
     * Shift plain text forward
     *
     * @param str plain text
     * @return cipher text
     * @see EncryptionStrategy#encrypt(java.lang.String)
     */
    @Override
    public String encrypt(String str) {
        return code(str, SHIFT);
    }

    /**
     * Shift cipher text backwards
     *
     * @param str cipher text
     * @return plain text
     * @see EncryptionStrategy#decrypt(java.lang.String)
     */
    @Override
    public String decrypt(String str) {
        return code(str, -SHIFT);
    }
}
