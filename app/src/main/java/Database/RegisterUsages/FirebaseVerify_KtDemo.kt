package Database.RegisterUsages

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

fun registerUser(username: String, password: String) {
    mAuth.createUserWithEmailAndPassword(username.trim() + "@dailytodo.com", password)
        .addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = mAuth.currentUser
                if (user != null) {
                    val uid = user.uid;

                    val ref: DatabaseReference =
                        FirebaseDatabase.getInstance().getReference("users");
                    ref.child(uid).child("name").setValue(username)
                } else {
                    Log.w("Register", "User is still null after delay.")
                }
            } else {
                Log.w("Register", "User registration failed $username", task.exception)
            }
        })
}


interface UsernameResCallback {
    fun onRes(exists: Boolean, username: String)
}

fun getSignedUsername(callback: UsernameResCallback) {
    val user: FirebaseUser ?= mAuth.currentUser
    if (user == null) {
        callback.onRes(false, null.toString())
        return
    }

    val uid = user.uid
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists() && snapshot.child("name").exists()) {
                val name = snapshot.child("name").toString()
                callback.onRes(true, name);
            } else callback.onRes(false, null.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("FirebaseVerify", "Error: " + error.message)
            callback.onRes(false, null.toString())
        }
    })
}


interface EmailCheckCallback {
    fun onCheck(exists: Boolean)
}

fun checkIfAlreadyAdded(username: String, callback: EmailCheckCallback) {
    val emailToCheck = username.trim() + "dailytodo.com"

    mAuth.fetchSignInMethodsForEmail(emailToCheck)
        .addOnCompleteListener({task ->
            if (task.isSuccessful) run {
                val signedUp: MutableList<String>? = task.getResult().signInMethods
                val exists = signedUp != null && !signedUp.isEmpty()
                callback.onCheck(exists)
            } else {
                Log.e("FirebaseVerify", "Error checking email existence", task.exception)
                callback.onCheck(false)
            }
        })
}
