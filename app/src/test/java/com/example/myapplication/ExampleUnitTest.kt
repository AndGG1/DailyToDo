package com.example.myapplication

import Database.RegisterUsages.decrypt
import Database.RegisterUsages.encrypt
import Database.RegisterUsages.registerUser
import androidx.work.ListenableWorker.Result.success
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

import Database.RegisterUsages.IsValidCallback
import Database.RegisterUsages.removeUser
import com.google.firebase.FirebaseApp


@RunWith(RobolectricTestRunner::class)
class DatabaseLogicUnitTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }
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
            if (exc == null) {
                success()
                return
            }

            //Init. Problems
            when (exc) {
                NoSuchAlgorithmException(), NoSuchPaddingException(), InvalidKeyException() -> {
                    fail("The chosen ALGO/PADDING/KEY is wrong!  -  " + exc.message)

                }
                IllegalBlockSizeException(), BadPaddingException() -> {
                    fail("The input caused trouble: The BLOCK_SIZE/PADDING  -  " + exc.message)

                }
                else -> {
                    fail("Unfamiliar problem occurred!  -  " + exc.message)
                }
            }
        })
    }

    /*
    Tests decryption logic, this means:
    Encoded String values, in our case, encrypted usernames, will get converted back to normal & readable text.
     */
    @Test
    fun decryptingTest() {
        val problematicInputs = arrayOf(
            // 🔐 Bad Padding / Corrupted Ciphertext
            "U2FsdGVkX1+abc123==",              // Truncated Base64 string
            "U2FsdGVkX1+/////==",               // All padding, no real data
            "U2FsdGVkX1+@#\$%^&*()",            // Non-Base64 characters
            "U2FsdGVkX1+Zm9vYmFyYmF6",          // Valid Base64 but not AES-encrypted
            "U2FsdGVkX1+Zm9vYmFyYmF6Zm9vYmFy",  // Wrong block size (not multiple of 16 bytes)

            // 🧨 Encoding / Non-UTF-8 Issues
            "U2FsdGVkX1+💥🔥👾",                // Emoji characters — not valid Base64
            "U2FsdGVkX1+Zm9vYmFy\nYmF6",        // Line breaks inside Base64
            "U2FsdGVkX1+Zm9vYmFyYmF6=",         // Valid Base64, but decrypted bytes aren't UTF-8
            "U2FsdGVkX1+Zm9vYmFyYmF6\u00FF",    // Contains invalid byte for UTF-8
            "U2FsdGVkX1+Zm9vYmFyYmF6\uDC00",   // Lone surrogate character (UTF-16 corruption)

            //Basic Names
            "Sara Panescu",
            "Emil Racovita",
            "Nicolas Nico Adri Godinac Goblin",
            "Yuyu"
        )

        problematicInputs.forEach { s ->
            runBlocking {
                val str = encrypt(s)
                val exc = decryptionFlawGenerator(str)
                if (exc == null) {
                    success()
                    return@runBlocking
                }

                //Init. Problems
                when (exc) {
                    NoSuchAlgorithmException(), NoSuchPaddingException(), InvalidKeyException() -> {
                        fail("The chosen ALGO/PADDING/KEY is wrong!  -  " + exc.message)

                    }

                    IllegalBlockSizeException(), BadPaddingException() -> {
                        fail("The input caused trouble: The BLOCK_SIZE/PADDING  -  " + exc.message)

                    }

                    else -> {
                        fail("Unfamiliar problem occurred!  -  " + exc.message)
                    }
                }
            }
        }
    }

    /*
    Tests registering logic, this is:
    An user will have his name converted to an email with the default "@dailytodo.com" address;
    This is, because we have to add them in Firebase, where their private uid, password and name will be retained safely;
    Future features will require the name and password, so it is mandatory that they are saved and easy to extract in the future..
     */
    @Test
    fun registeringUserTest() {
        FirebaseApp.initializeApp(context)

        val new_username = "Dummy_DailyToDo_TooLongName"
        registerUser(new_username.trim().toLowerCase(), "12345678", object : IsValidCallback {
            override fun onRes(isValid: Boolean, uid: String?) {
                if (!isValid || uid == null) fail("New User unable to join!")
                removeUser()
            }
        })

        val old_username = "Andrei"
        registerUser(old_username.trim().toLowerCase(), "12345678", object : IsValidCallback {
            override fun onRes(isValid: Boolean, uid: String?) {
                if (isValid) fail("Old User shouldn't be able to join!")
            }
        })
    }

    //Tests if the method returns true/false when the user does/n't exist correctly.
    @Test
    fun isUserAlreadyRegisteredTest() {
        //TODO
    }
}

fun encryptionFlawGenerator(wrongInput: String): Exception? {
    try {
        runBlocking { encrypt(wrongInput)}
    } catch (e: Exception) {
        return e
    }
    return null
}

fun decryptionFlawGenerator(wrongInput: String) : Exception? {
    try {
        decrypt(wrongInput)
    } catch (e: Exception) {
        return e
    }
    return null
}

