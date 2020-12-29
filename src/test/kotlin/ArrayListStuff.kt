import kotlin.math.E

fun main() {
    var list = arrayOfNulls<Int>(10)

    while (true) {
        list = list.plus(1)
        println("Length: ${list.size}")
    }

}