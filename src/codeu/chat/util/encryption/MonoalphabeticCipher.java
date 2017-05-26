package codeu.chat.util.encryption;

/**
 * A monoalphabetic cipher uses fixed substitution over the entire message, in
 * this case, this algorithm substitutes the alphabet order with the order of a
 * standard computer keyboard
 * <p>
 * Disadvantage: 1. hard-coded 2. does not change input other than upper and
 * lower cases of the 26 alphabet and spaces
 *
 * @author Kuan Chi Chen
 */
public class MonoalphabeticCipher implements EncryptionStrategy {
    /**
     * Char array in alphabet order with space
     **/
    private char alphabet[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * Char array in computer keyboard order with space
     **/
    private char keyboard[] = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j',
            'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A',
            'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'};

    @Override
    /**
     * Perform encryption
     *
     * @param s
     *            plainText
     * @return cipherText
     * @see EncryptionStrategy#encrypt(java.lang.String)
     */
    public String encrypt(String s) {
        // convert input into char array
        char result[] = new char[(s.length())];
        // substitute each alphabet with its correspondence on the keyboard
        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < 52; j++) {
                if (alphabet[j] == s.charAt(i)) {
                    result[i] = keyboard[j];
                    break;
                } else result[i] = s.charAt(i);
            }
        }
        return (new String(result));
    }

    @Override
    /**
     * Reverse the substitution
     *
     * @param s
     *            cipherText
     * @return plainText
     * @see EncryptionStrategy#decrypt(java.lang.String)
     */
    public String decrypt(String s) {
        char result[] = new char[(s.length())];
        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < 52; j++) {
                if (keyboard[j] == s.charAt(i)) {
                    result[i] = alphabet[j];
                    break;
                } else result[i] = s.charAt(i);
            }
        }
        return (new String(result));
    }

}