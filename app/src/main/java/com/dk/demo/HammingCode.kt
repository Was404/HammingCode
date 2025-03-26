object HammingCode {

// Кодирование строки в бинарный формат с исправлением ошибок
    data class DecodeResult(
        val text: String, // Декодированный текст
        val errorCount: Int, // Кол-во найденных ошибок
        val totalBlocks: Int, // Кол-во 7-битных блоков
        val errorDetails: String // Описание ошибок
    )

    fun encode(message: String): String {
        val binaryStr = message.toBinaryString() // Текст → биты UTF-8
        val paddedStr = addPadding(binaryStr, 4) // Выравнивание до 4 бит
        return paddedStr.chunked(4).joinToString("") { encode4Bit(it) }
    }

    // Декодирование с исправлением одной ошибки в 7-битном блоке
    fun decode(encodedMessage: String): String {
        val corrected = encodedMessage.chunked(7).map { correctErrors(it) }.joinToString("") // Разбивает закодированную строку на 7-битные блоки.Исправляет ошибки в каждом блоке.
        val dataBits = corrected.mapIndexed { index, c -> // Извлекает исходные 4 бита данных из каждого блока
            when ((index % 7)) {
                2, 4, 5, 6 -> c
                else -> null
            }
        }.filterNotNull().joinToString("")
        return dataBits.binaryStringToText() // Преобразует биты обратно в текст.
    }

    private fun encode4Bit(bits: String): String {
        require(bits.length == 4) { "Input must be 4 bits" }
        // Принимает 4 бита данных
        val d1 = bits[0].digitToInt()
        val d2 = bits[1].digitToInt()
        val d3 = bits[2].digitToInt()
        val d4 = bits[3].digitToInt()
        // Вычисляет 3 контрольных бита
        val r1 = d1 xor d2 xor d4
        val r2 = d1 xor d3 xor d4
        val r3 = d2 xor d3 xor d4

        return "$r1$r2$d1$r3$d2$d3$d4" // Возвращает 7-битный блок в формате
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

        val errorPosition = s1 + s2 * 2 + s3 * 4 - 1 // Исправление бита

        if (errorPosition >= 0) { // Инвертирует ошибочный бит (если ошибка найдена)
            val corrected = bits.toCharArray()
            corrected[errorPosition] = if (corrected[errorPosition] == '0') '1' else '0'
            return String(corrected)
        }

        return bits
    }

    // Вспомогательные функции
    private fun String.toBinaryString(): String { // Конвертирует текст в бинарную строку через UTF-8
        return this.toByteArray(Charsets.UTF_8) // Используем UTF-8
            .joinToString("") { byte ->
                byte.toInt().and(0xFF).toString(2).padStart(8, '0')
            }
    }

    private fun addPadding(bits: String, blockSize: Int): String { // Добавляет нули в конец, чтобы длина была кратна blockSize
        val padding = (blockSize - (bits.length % blockSize)) % blockSize
        return bits + "0".repeat(padding)
    }

    private fun String.binaryStringToText(): String { // Преобразует бинарную строку обратно в текст (UTF-8)
        val byteArray = this.chunked(8).map { binaryByte ->
            val byteStr = binaryByte.padEnd(8, '0').take(8)
            byteStr.toInt(2).toByte()
        }.toByteArray()

        return String(byteArray, Charsets.UTF_8) // Декодируем через UTF-8
            .replace("\u0000", "") // Удаляем нулевые символы от паддинга
    }

    private fun extractDataBits(encoded: String): String { // Извлекает данные из 7-битных блоков, отбрасывая контрольные биты
        return encoded.chunked(7).joinToString("") { chunk ->
            if (chunk.length < 7) "" else "${chunk[2]}${chunk[4]}${chunk[5]}${chunk[6]}"
        }
    }


    fun decodeWithErrorInfo(encodedMessage: String): DecodeResult { // Возвращает DecodeResult с детальной информацией об ошибках
        val (corrected, errors) = correctErrorsWithInfo(encodedMessage)
        val dataBits = extractDataBits(corrected) // Используем функцию извлечения данных
        val totalBlocks = encodedMessage.chunked(7).size // Считаем блоки

        return DecodeResult(
            text = dataBits.binaryStringToText(),
            errorCount = errors,
            totalBlocks = totalBlocks, // 4. Передаем в результат
            errorDetails = if (errors > 0) "Найдено и исправлено $errors ошибок"
            else "Ошибок не обнаружено"
        )
    }

    private fun correctErrorsWithInfo(bits: String): Pair<String, Int> { // Исправляет ошибки и подсчитывает их количество
        var errorCount = 0
        val corrected = bits.chunked(7).joinToString("") { chunk ->
            val (fixedChunk, hasError) = correctChunk(chunk)
            if (hasError) errorCount++
            fixedChunk
        }
        return Pair(corrected, errorCount)
    }

    private fun correctChunk(chunk: String): Pair<String, Boolean> {
        if (chunk.length < 7) return Pair(chunk, false)

        val corrected = correctErrors(chunk)
        return Pair(corrected, corrected != chunk)
    }

    fun introduceErrors(encoded: String, errorBlocks: Int = 1): String { // Искусственно добавляет ошибки в закодированные данные для тестирования
        val chunks = encoded.chunked(7).toMutableList()
        repeat(errorBlocks.coerceAtMost(chunks.size)) { index ->
            val chunk = chunks[index].toCharArray()
            val errorPos = (0 until 7).random()
            chunk[errorPos] = if (chunk[errorPos] == '0') '1' else '0'
            chunks[index] = String(chunk)
        }
        return chunks.joinToString("")
    }


}