import org.bouncycastle.jce.provider.BouncyCastleProvider
import src.KeySet
import java.security.Security

fun main() {
    Security.addProvider(BouncyCastleProvider())
    KeySet.isValidKey(byteArrayOf(1,2,3))
}