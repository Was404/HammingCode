class HammingCode {
/*
Требует модификации: демонстрация исправления ошибок
*/
// Кодирование строки в бинарный формат с исправлением ошибок
    fun encode(message: String): String {
        val binaryStr = message.toBinaryString()
        val paddedStr = addPadding(binaryStr, 4)
        return paddedStr.chunked(4).joinToString("") { encode4Bit(it) }
    }

    // Декодирование с исправлением одной ошибки в 7-битном блоке
    fun decode(encodedMessage: String): String {
        val corrected = encodedMessage.chunked(7).map { correctErrors(it) }.joinToString("")
        val dataBits = corrected.mapIndexed { index, c ->
            when ((index % 7)) {
                2, 4, 5, 6 -> c
                else -> null
            }
        }.filterNotNull().joinToString("")
        return dataBits.binaryStringToText()
    }

    private fun encode4Bit(bits: String): String {
        require(bits.length == 4) { "Input must be 4 bits" }

        val d1 = bits[0].digitToInt()
        val d2 = bits[1].digitToInt()
        val d3 = bits[2].digitToInt()
        val d4 = bits[3].digitToInt()

        val r1 = d1 xor d2 xor d4
        val r2 = d1 xor d3 xor d4
        val r3 = d2 xor d3 xor d4

        return "$r1$r2$d1$r3$d2$d3$d4"
    }

    private fun correctErrors(bits: String): String {
        if (bits.length < 7) return bits

        val p1 = bits[0].digitToInt()
        val p2 = bits[1].digitToInt()
        val d1 = bits[2].digitToInt()
        val p3 = bits[3].digitToInt()
        val d2 = bits[4].digitToInt()
        val d3 = bits[5].digitToInt()
        val d4 = bits[6].digitToInt()

        // Вычисляем синдромы
        val s1 = p1 xor d1 xor d2 xor d4
        val s2 = p2 xor d1 xor d3 xor d4
        val s3 = p3 xor d2 xor d3 xor d4

        val errorPosition = s1 + s2 * 2 + s3 * 4 - 1

        if (errorPosition >= 0) {
            val corrected = bits.toCharArray()
            corrected[errorPosition] = if (corrected[errorPosition] == '0') '1' else '0'
            return String(corrected)
        }

        return bits
    }

    // Вспомогательные функции
    private fun String.toBinaryString(): String {
        return this.map { char ->
            char.code.toString(2).padStart(8, '0')
        }.joinToString("")
    }

    private fun addPadding(bits: String, blockSize: Int): String {
        val padding = (blockSize - (bits.length % blockSize)) % blockSize
        return bits + "0".repeat(padding)
    }

    private fun String.binaryStringToText(): String {
        return this.chunked(8).map { binaryByte ->
            val byteValue = binaryByte.padEnd(8, '0').take(8).toInt(2)
            byteValue.toChar()
        }.joinToString("").trimEnd { it == '\u0000' }
    }
}