class HammingCode {
/*
Требует модификации: кодировка вход стрки по ASCII, демонстрация исправления ошибок
*/
    // Возвращает закодированное сообщение с использованием кода Хэмминга
    fun encode(message: String): String {
        val messageBits = message.map { it.toString().toInt() }
        val m = messageBits.size
        val r = calculateRedundantBits(m)
        val totalBits = m + r
        val codeword = IntArray(totalBits)

        var j = 0
        for (i in 1..totalBits) {
            if (isPowerOfTwo(i)) {
                // Устанавливаем контрольные биты на 0
                codeword[i - 1] = 0
            } else {
                codeword[i - 1] = messageBits[j]
                j++
            }
        }

        // Вычисление значений контрольных бит
        for (i in 1..r) {
            val parityPosition = (1 shl (i - 1))
            var parity = 0

            for (j in 1..totalBits) {
                if (j and parityPosition != 0) {
                    parity = parity xor codeword[j - 1]
                }
            }

            codeword[parityPosition - 1] = parity
        }

        return codeword.joinToString("")
    }

    // Декодирует сообщение и исправляет одну ошибку, если она есть
    fun decode(codeword: String): Pair<String, String> {
        val bits = codeword.map { it.toString().toInt() }.toIntArray()
        val totalBits = bits.size
        val r = calculateRedundantBits(totalBits)  // Исправлено вычисление r
        var errorPosition = 0

        for (i in 1..r) {
            val parityPosition = (1 shl (i - 1))
            var parity = 0

            for (j in 1..totalBits) {
                if (j and parityPosition != 0) {
                    parity = parity xor bits[j - 1]
                }
            }

            if (parity != 0) {
                errorPosition += parityPosition
                break  // Исправлено: выход из цикла после обнаружения ошибки
            }
        }

        if (errorPosition != 0) {
            println("Ошибка в позиции: $errorPosition")
            bits[errorPosition - 1] = if (bits[errorPosition - 1] == 0) 1 else 0 // Исправляем ошибку
            println("Исправленное сообщение: ${bits.joinToString("")}") // Выводим исправленное сообщение
        } else {
            println("Ошибок не найдено.")
        }

        // Извлечение оригинального сообщения
        val originalMessage = bits.filterIndexed { index, _ -> !isPowerOfTwo(index + 1) }
            .joinToString("")

        return Pair(originalMessage, bits.joinToString(""))
    }

    // Проверяет, является ли число степенью двойки
    private fun isPowerOfTwo(n: Int): Boolean {
        return (n and (n - 1)) == 0
    }

    // Вычисляет количество контрольных бит для заданного количества бит сообщения
    private fun calculateRedundantBits(m: Int): Int {
        var r = 0
        while (Math.pow(2.0, r.toDouble()).toInt() < m + r + 1) {
            r++
        }
        return r
    }
}