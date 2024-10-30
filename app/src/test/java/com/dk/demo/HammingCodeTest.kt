import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class HammingCodeTest {

    private val hammingCode = HammingCode()

    @Test
    fun testEncode() {
        val message = "1011"
        val encodedMessage = hammingCode.encode(message)
        assertEquals("01110011", encodedMessage)
    }

    @Test
    fun testDecodeWithoutError() {
        val codeword = "01110011"
        val decodedMessage = hammingCode.decode(codeword)
        assertEquals("1011", decodedMessage.first)
    }

    @Test
    fun testDecodeWithError() {
        val codeword = "01111011"
        val decodedMessage = hammingCode.decode(codeword)
        assertEquals("1011", decodedMessage.second) //  Исправлена  проверка  на  исправленное  сообщение
    }
}
