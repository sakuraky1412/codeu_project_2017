package codeu.chat.util.encryption;

/**
 * The Vigenère cipher is a method of encrypting alphabetic text by using a
 * series of interwoven Caesar ciphers based on the letters of a keyword. It is
 * a form of polyalphabetic substitution.
 * <p>
 * Bascially, it encrypts a message with a key and modulo 26. Disadvantage: only
 * accepts and returns upper case alphabets, without spaces
 * <p>
 * Help from the internet obtained.
 *
 * @author Kuan Chi Chen
 */
public class VigenereCipher implements EncryptionStrategy {
    /**
     * The key of the algorithm
     **/
    private static final String KEY = "VIGENERECIPHER";
    private boolean[] isUpperCase;

    /**
     * Final output
     **/
    private String result;

    /**
     * Perform encryption
     *
     * @param plainText input
     * @return cipherText
     * @see EncryptionStrategy#encrypt(java.lang.String)
     */
    @Override
    public String encrypt(String plainText) {
        result = "";
        isUpperCase = new boolean[plainText.length()];

        for (int k = 0; k < plainText.length(); k++) {
            char c = plainText.charAt(k);
            if (c > 'A' && c < 'Z') isUpperCase[k] = true;
            else isUpperCase[k] = false;
        }

        plainText = plainText.toUpperCase();

        for (int i = 0, j = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);
            // make sure that the char is an upper case alphabet
            if (c < 'A' || c > 'Z') {
                result += c;
                continue;
            }
            // add the corresponding char in the key to the current char from
            // plainText
            // mod 26 and add the 'A' value to get the corresponding value in
            // the Vigenère table
            result += (char) ((c + KEY.charAt(j)) % 26 + 'A');
            // repeats keyword until it matches the length of the plain text
            j = ++j % KEY.length();
        }

        return getString();
    }

    @Override
    /**
     * Perform decryption
     *
     * @param cipherText
     * @return plainText
     * @see EncryptionStrategy#decrypt(java.lang.String)
     */
    public String decrypt(String cipherText) {
        result = "";
        isUpperCase = new boolean[cipherText.length()];

        for (int k = 0; k < cipherText.length(); k++) {
            char c = cipherText.charAt(k);
            if (c > 'A' && c < 'Z') isUpperCase[k] = true;
            else isUpperCase[k] = false;
        }

        cipherText = cipherText.toUpperCase();
        for (int i = 0, j = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);
            // make sure that the char is an upper case alphabet
            if (c < 'A' || c > 'Z') {
                result += c;
                continue;
            }
            // subtract the the corresponding char in the key from the current
            // char in plainText, in case a negative value is returned, add 26
            // to it
            // mod 26 and add the 'A' value to retrieve the original value in
            // the Vigenère table
            result += (char) ((c - KEY.charAt(j) + 26) % 26 + 'A');
            // repeats keyword until it matches the length of the plain text
            j = ++j % KEY.length();
        }

        return getString();
    }

    private String getString() {
        String resultText = "";

        for (int l = 0; l < result.length(); l++) {
            char resultc = result.charAt(l);
            if (!isUpperCase[l]) resultText += Character.toLowerCase(resultc);
            else resultText += resultc;
        }

        return resultText;
    }
}