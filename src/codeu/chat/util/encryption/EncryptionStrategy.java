package codeu.chat.util.encryption;

/**
 * This class uses the Strategy pattern since we have multiple encryption
 * strategies to choose among
 *
 * @author Kuan-Chi Chen
 */
public interface EncryptionStrategy {
    /**
     * Performs encryption
     *
     * @param str input
     * @return String encrypted output
     */
    String encrypt(String str);

    /**
     * Performs decryption
     *
     * @param str input
     * @return String decrypted output
     */
    String decrypt(String str);
}
