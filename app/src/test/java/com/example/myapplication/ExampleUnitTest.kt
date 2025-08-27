package com.example.myapplication

import Database.RegisterUsages.encrypt
import androidx.work.ListenableWorker.Result.success
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

@RunWith(RobolectricTestRunner::class)
class DatabaseLogicUnitTest {

    /*
    Tests encryption logic, this means:
    String values, in out case, usernames, will be converted to unreadable bite data.
     */
    @Test
    fun encryptingTest() {
        //val mockContext =
        val testUsernames = listOf(
            //  Special characters (10)
            "user!@#\$%^&*()", "<>?\"{}|~", "☃️", "🔥", "💥", "✨", "👨‍💻", "👩‍🚀", "👽", "𝓤𝓼𝓮𝓻𝓝𝓪𝓶𝓮",

            //  Empty or minimal (2)
            "", " ",

            //  Long strings (1)
            "LongUsernameWithNoSpacesButLotsOfCharactersToTestBlockSizeHandling",

            //  Base64-like but not valid (5)
            "U29tZUJyb2tlbkVuY3J5cHRlZERhdGE=", "=====", "Zm9vYmFy", "U3VwZXJNYWxmb3JtZWRCYXNlNjQ=", "QmFkUGFkZGluZw=="
        )

        testUsernames.forEach({s ->
            val exc: Exception? = encryptionFlawGenerator(s)
            //Init. Problems
            if (exc != null
                && exc == NoSuchAlgorithmException() || exc == NoSuchPaddingException() || exc == InvalidKeyException()) {
                fail("The chosen ALGO/PADDING/KEY is wrong!  -  " + exc.message)

            } else if (exc != null
                && exc == IllegalBlockSizeException() || exc == BadPaddingException()) {
                fail("The input caused trouble: The BLOCK_SIZE/PADDING  -  " + exc.message)

            } else if (exc != null) {
                fail("Unfamiliar problem occurred!  -  " + exc.message)

            } else success()
        })
    }

    /*
    Tests decryption logic, this means:
    Encoded String values, in our case, encrypted usernames, will get converted back to normal & readable text.
     */
    @Test
    fun decryptingTest() {

    }

    /*
    Tests registering logic, this is:
    An user will have his name converted to an email with the default "@dailytodo.com" address;
    This is, because we have to add them in Firebase, where their private uid, password and name will be retained safely;
    Future features will require the name and password, so it is mandatory that they are saved and easy to extract in the future..
     */
    @Test
    fun registeringUserTest() {

    }

    //Tests if the method returns true/false when the user does/n't exist correctly.
    @Test
    fun isUserAlreadyRegisteredTest() {

    }
}

fun encryptionFlawGenerator(wrongInput: String): Exception? {
    try {
        runBlocking { encrypt(wrongInput)}
    } catch (e: Exception) {
        return e;
    }
    return null
}
