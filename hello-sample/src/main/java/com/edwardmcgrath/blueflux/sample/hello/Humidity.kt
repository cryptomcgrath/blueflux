import kotlin.math.pow

fun testHumidity() {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    val ba = "690dbe6e".decodeHex()
    println("humidity="+ba.toHumidity())
}

fun ByteArray.toHumidity(): Float {
    //# float var3 = relativeHumidityFromRawHumidity((var5[0] & 255) + ((var5[1] & 15) << 8));
    return relativeHumidityFromRawHumidity(
            (this[0].toInt() and 255) + ((this[1].toInt() and 15) shl 8)
    )
}


fun relativeHumidityFromRawHumidity(i: Int): Float {
    val rh = i.toFloat() / 2.0.pow(12.0) * 125f - 6f
    return when {
        rh < 0f -> 0f
        rh > 100f -> 100f
        else -> rh.toFloat()
    }
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
}