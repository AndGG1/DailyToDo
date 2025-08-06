package Database.RegisterUsages;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;


public class FirebaseVerify {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void registerUser(String username, String password) {

        mAuth.createUserWithEmailAndPassword(username + "@dailytodo.com", password)
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

    public static boolean getSignedInUsername(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.w("FirebaseVerify", "No user is signed in.");
            return false;
        }

        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                Log.w("FirebaseVerify", "Name: " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FirebaseVerify", "Error: " + databaseError.getMessage());
            }
        });

        return true;
    }
}
