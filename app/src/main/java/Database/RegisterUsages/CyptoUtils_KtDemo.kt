package Database.RegisterUsages

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

val KEY_VALUE = "1234567890123456"
val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
val secretKey = SecretKeySpec(KEY_VALUE.toByteArray(), "AES")

suspend fun encrypt(input: String) : String {
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)

    val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encrypted, Base64.DEFAULT)
}

fun decrypt(input: String) : String {
    cipher.init(Cipher.DECRYPT_MODE, secretKey)

    val decrypted = Base64.decode(input, Base64.DEFAULT)
    return String(cipher.doFinal(decrypted), Charsets.UTF_8)
}
