package Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.function.Consumer;

//TODO: Fix
public class FirebaseVerify {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void registerUser(String username, String password, Context context) {
        mAuth.createUserWithEmailAndPassword(username + "@dailytodo.com", password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 🕒 Wait for 750ms before accessing the user object
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                prefs.edit()
                                        .putString("user_uid", uid)
                                        .putString("username", username)
                                        .putBoolean("hasSignedInBefore", true)
                                        .apply();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                ref.child(uid).child("name").setValue(username);
                            } else {
                                Log.w("Register", "User is still null after delay.");
                            }
                        }, 750);
                    } else {
                        Log.w("Register", "User registration failed", task.getException());
                    }
                });
    }

    public static void getSignedInUsername(Context context, Consumer<String> onResult) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("name");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    onResult.accept(name != null ? name : "Unknown");
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("FetchName", "Database error: ", error.toException());
                    onResult.accept("Error");
                }
            });
        } else {
            Log.w("FetchName", "No user currently signed in.");
            onResult.accept("No user");
        }
    }
}