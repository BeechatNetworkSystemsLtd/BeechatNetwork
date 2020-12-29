import com.digi.xbee.api.utils.HexUtils

fun main() {

    var i = 0
    var hexLength = 0

    while (hexLength <= 3) {
        val hexString = HexUtils.integerToHexString(i, 1)
        hexLength = hexString.length
        println("$i: $hexString ($hexLength)")
        i++
    }
}
