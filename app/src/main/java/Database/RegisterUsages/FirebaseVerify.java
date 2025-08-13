package Database.RegisterUsages;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;


public class FirebaseVerify {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void registerUser(String username, String password) {
        mAuth.createUserWithEmailAndPassword(username.trim() + "@dailytodo.com", password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                ref.child(uid).child("name").setValue(username);

                            } else {
                                Log.w("Register", "User is still null after delay.");
                            }
                    } else {
                        Log.w("Register", "User registration failed " + username, task.getException());
                    }
                });
    }


    public interface UsernameResultCallback {
        void onResult(boolean exists, String username);
    }

    public static void getSignedInUsername(UsernameResultCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onResult(false, null);
            return;
        }

        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("name").exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    callback.onResult(true, name);
                } else {
                    callback.onResult(false, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseVerify", "Error: " + databaseError.getMessage());
                callback.onResult(false, null);
            }
        });
    }

    public interface EmailCheckCallback {
        void onResult(boolean exists);
    }


    //TODO: Fix!
    public static void checkIfEmailExists(String username, EmailCheckCallback callback) {
        String emailToCheck = username.trim() + "@dailytodo.com";

        mAuth.fetchSignInMethodsForEmail(emailToCheck)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> signInMethods = task.getResult().getSignInMethods();
                        boolean exists = signInMethods != null && !signInMethods.isEmpty();
                        callback.onResult(exists);
                    } else {
                        Log.e("FirebaseVerify", "Error checking email existence", task.getException());
                        callback.onResult(false); // You might want to handle this differently
                    }
                });
    }

}
