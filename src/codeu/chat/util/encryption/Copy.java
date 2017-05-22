package codeu.chat.util.encryption;

/**
 * The plain text is copied to cipher text and vice versa
 *
 * @author Kuan Chi Chen
 */
public class Copy implements EncryptionStrategy {

    /**
     * Copy plain text
     *
     * @param str input
     * @return input
     * @see EncryptionStrategy#encrypt(java.lang.String)
     */
    @Override
    public String encrypt(String str) {
        return str;
    }

    /**
     * Copy cipher text
     *
     * @param str input
     * @return input
     * @see EncryptionStrategy#decrypt(java.lang.String)
     */
    @Override
    public String decrypt(String str) {
        return str;
    }

}
