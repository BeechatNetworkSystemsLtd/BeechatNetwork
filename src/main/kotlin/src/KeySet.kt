/*
 * This file is part of BeeChat.
 *
 *     BeeChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BeeChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BeeChat.  If not, see <https://www.gnu.org/licenses/>.
 */



package src

import org.bouncycastle.jce.ECNamedCurveTable
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import java.util.logging.Logger
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.SecretKeySpec

class KeySet() { // TODO: 12/26/20 Write documentation for this! 

    val logger = Logger.getAnonymousLogger()

    val generator = KeyPairGenerator.getInstance("ECDH", "BC")
    val agreement = KeyAgreement.getInstance("ECDH", "BC")
    val keyPair: KeyPair

    lateinit var sharedKey: SecretKeySpec
    var cipher = Cipher.getInstance("AES", "BC")
    init {
        logger.info("Initializing...")
        generator.initialize(ECNamedCurveTable.getParameterSpec("c2pnb163v2")) // TODO: 12/28/20 Shitty EC, just a stopgap measure. Get a better curve 
        keyPair = generator.generateKeyPair()
        agreement.init(keyPair.private)
    }
    
    fun createSecret(bytes: ByteArray) {
        logger.info("Generating secret key...")
        if (isValidKey(bytes)) {
            logger.info("Key is valid!")

            val factory = KeyFactory.getInstance("ECDH", "BC")
            agreement.doPhase(factory.generatePublic(X509EncodedKeySpec(bytes)), true)
            sharedKey = SecretKeySpec(agreement.generateSecret(), 0, 16, "ECDH")


        } else {
            logger.warning("Key is invalid!")
        }
    }

    fun encryptData(bytes: ByteArray): ByteArray {
        logger.info("Encrypting data...")
        try {
            cipher.init(Cipher.ENCRYPT_MODE, sharedKey)
            return cipher.doFinal(bytes)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return "ERROR".encodeToByteArray()
    }

    fun decryptData(bytes: ByteArray): ByteArray {
        logger.info("Decrypting data...")
        cipher.init(Cipher.DECRYPT_MODE, sharedKey)
        return cipher.doFinal(bytes)
    }

    companion object {
        fun isValidKey(bytes: ByteArray): Boolean {
            return try {
                val spec = X509EncodedKeySpec(bytes)
                val factory = KeyFactory.getInstance("ECDH", "BC")
                factory.generatePublic(spec)
                true
            } catch (e: GeneralSecurityException) {
                println(e.message)
                false
            }
        }
    }

}