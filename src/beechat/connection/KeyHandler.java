package beechat.connection;

import com.digi.xbee.api.exceptions.XBeeException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECKeySpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Represents the keys and generators associated with a remote node.
 */
public class KeyHandler {

    KeyAgreement keyAgreement;
    KeyPair keyPair;
    boolean hasKey = false;

    Cipher encryptionCipher;
    Cipher decryptionCipher;

    public KeyHandler (Node node) {
        try {
            keyAgreement = KeyAgreement.getInstance("ECDH", "BC");

            KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDH", "BC");
            generator.initialize(ECNamedCurveTable.getParameterSpec("secp192k1"));

            keyPair = generator.generateKeyPair();
            keyAgreement.init(keyPair.getPrivate());

            node.getLOCAL_DEVICE().sendData(node.getREMOTE_DEVICE(), keyPair.getPublic().getEncoded());

        } catch (GeneralSecurityException e) {
            System.err.println("Couldn't generate a key pair: " + e.getMessage());
        } catch (XBeeException e) {
            System.err.println("Couldn't send public key: " + e.getMessage());
        }

    }

    void receiveKey(byte[] key) {

        try {

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key, "ECDH");

            KeyFactory factory = KeyFactory.getInstance("ECDH", "BC");
            PublicKey publicKey = factory.generatePublic(keySpec);

            keyAgreement.doPhase(publicKey, true);

            SecretKeySpec sharedSecret = new SecretKeySpec(keyAgreement.generateSecret(), "AES");

            System.out.println("Generated shared secret!");

            encryptionCipher = Cipher.getInstance("AES", "BC");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, sharedSecret);

            decryptionCipher = Cipher.getInstance("AES", "BC");
            decryptionCipher.init(Cipher.DECRYPT_MODE, sharedSecret);

            System.out.println("Ready!");

            hasKey = true;

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    /**
     * Receive and decrypt encrypted data
     * @param data the data to be decrypted
     * @return decrypted data
     */
    byte[] decrypt(byte[] data) throws GeneralSecurityException {
        return decryptionCipher.doFinal(data);
    }

    /**
     * Encrypt data
     * @param data the data to be encrypted and sent
     * @return encrypted data
     */
     byte[] encrypt(byte[] data) throws GeneralSecurityException {
         return encryptionCipher.doFinal(data);
    }

}
