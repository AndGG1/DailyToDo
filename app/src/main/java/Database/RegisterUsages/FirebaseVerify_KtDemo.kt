package Database.RegisterUsages

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
val database: DatabaseReference = Firebase.database.reference

interface IsValidCallback {
    fun onRes(isValid: Boolean, uid: String?)
}

fun registerUser(username: String, password: String, callback: IsValidCallback) {
    mAuth.createUserWithEmailAndPassword(username.trim() + hashUid(mAuth.currentUser?.uid.toString()) + "@dailytodo.com", password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = mAuth.currentUser
                if (user != null) {
                    val uid = user.uid
                    val ref = FirebaseDatabase.getInstance().getReference("users")

                    // Store full User object under users/uid
                    val newUser = User(
                        name = username,
                        numberOfActions = 0,
                        lastTimeJoined = System.currentTimeMillis()
                    )
                    Log.d("test+nga", uid)

                    ref.child(uid).setValue(newUser)

                    callback.onRes(true, uid)
                } else callback.onRes(false, null)
            } else {
                callback.onRes(false, null)
                Log.w("Register", "User registration failed $username", task.exception)
            }
        }
}

// Should be used only on unit tests
fun removeUser() {
    mAuth.currentUser?.delete()
}

interface UsernameResCallback {
    fun onRes(exists: Boolean, username: String)
}

fun getSignedUsername(callback: UsernameResCallback) {
    val user: FirebaseUser? = mAuth.currentUser
    if (user == null) {
        callback.onRes(false, null.toString())
        return
    }

    val uid = user.uid
    val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)

    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists() && snapshot.child("name").exists()) {
                val name = snapshot.child("name").getValue<String>() ?: ""
                callback.onRes(true, name)
            } else callback.onRes(false, null.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("FirebaseVerify", "Error: " + error.message)
            callback.onRes(false, null.toString())
        }
    })
}

fun getCurrUserActivity(username: String) {
    val uid = mAuth.currentUser?.uid ?: return
    val userRef = database.child("users").child(uid)

    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val activity = snapshot.getValue<User>()?.numberOfActions

            if (activity != null) {
                addNewUser(uid, username, activity + 1)
            } else {
                addNewUser(uid, username, 1)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // do nothing
        }
    })
}

fun addNewUser(uid: String, userName: String, activity: Int) {
    val newUser = User(
        name = userName,
        numberOfActions = activity,
        lastTimeJoined = System.currentTimeMillis()
    )

    val usersRef = database.child("users")

    usersRef.child(uid).setValue(newUser)
        .addOnFailureListener {
            Log.d("test+nga", "user firebase FAIL")
        }
}

data class User(
    var name: String? = null,
    var numberOfActions: Int? = null,
    var lastTimeJoined: Long = System.currentTimeMillis()
)