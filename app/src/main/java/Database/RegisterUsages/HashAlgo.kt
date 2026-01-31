package Database.RegisterUsages

import java.security.MessageDigest

fun hashUid(s: String) : String {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(s.toByteArray())
    val bs = md.digest()

    return bs.joinToString("") {"%02x".format(it)}.substring(0, 10)
}