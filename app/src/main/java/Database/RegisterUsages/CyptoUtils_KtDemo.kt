package Database.RegisterUsages

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

val ALGORITHM = "AES"
val TRANSFORMATION = "AES"
val key = "1234567890123456".toByteArray()

suspend fun encrypt(input: String) : String {
    try {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypted = cipher.doFinal(input.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    } catch (e: Exception) {
        return "-1"
    }
}

fun decrypt(input: String) : String {
    try {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decoded = Base64.decode(input, Base64.DEFAULT)
        val decrypted = cipher.doFinal(decoded)
        return decrypted.toString(StandardCharsets.UTF_8)
    } catch (e: Exception) {
        return "-1"
    }
}
