package com.mobileapps.walkbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobileapps.walkbuddy.models.Destination;
import com.mobileapps.walkbuddy.models.Route;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kurti on 11/20/2017.
 */
@VisibleForTesting
public class TestActivity extends AppCompatActivity implements
        SaveRouteFragment.OnSaveRouteFragmentInteractionListener,
        AccountFragment.OnAccountFragmentInteractionListener {
    private static final String TAG = "TestActivity";

    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    public FirebaseUser firebaseUser;

    public List<Destination> destinations = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.container);
        setContentView(frameLayout);

        mAuth = FirebaseAuth.getInstance(); // Firebase mAuth instance
        firebaseUser = mAuth.getCurrentUser(); // Firebase user reference
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Database reference instance

        if(mAuth.getCurrentUser() != null) {
            // Destinations
            mDatabase.child("users").child(mAuth.getUid()).child("destinations").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                                for(DataSnapshot snap : it) {
                                    destinations.add(snap.getValue(Destination.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.toString());
                        }
                    }
            );
        }
    }

    @Override
    public void cancelSaveRoute() {

    }

    @Override
    public void saveRoute(String destinationName, String startLocationName, List<Double> userLat, List<Double> userLng, List<Double> poiLat, List<Double> poiLng, long timeInMillis) {
        List<Route> routes = null;
        Route newRoute = new Route(destinationName, startLocationName, timeInMillis, userLat, userLng, poiLat, poiLng);
        Destination destinationToPut = null;
        String trimmedName = destinationName;
        trimmedName = trimmedName.replaceAll("\\P{Print}", "");
        trimmedName = trimmedName.replaceAll("\\.|\\$|\\[|\\]|#|/", "");

        if (firebaseUser != null) {
            final DatabaseReference destinationReference = mDatabase.child("users").child(mAuth.getUid()).child("destinations");

            for (Destination d : destinations) {
                if (d.getDestinationName().equals(trimmedName)) {
                    destinationToPut = d;
                    routes = d.getRoutes();
                }
            }

            if (destinationToPut != null) {
                routes.add(newRoute);
                destinationToPut = new Destination(routes, destinationToPut.getDestinationName());
                Map<String, Object> updatedDestination = new HashMap<>();
                updatedDestination.put(trimmedName, destinationToPut);
                destinationReference.updateChildren(updatedDestination);
            } else {
                routes = new ArrayList<>();
                routes.add(newRoute);
                destinationToPut = new Destination(routes, destinationName);
                destinationReference.child(trimmedName).setValue(destinationToPut);
            }

            Toast.makeText(TestActivity.this, "Your route has been saved", Toast.LENGTH_SHORT).show();
        }

        refreshTestActivity();
    }

    public void deleteDestination(String destinationName) {
        if (firebaseUser != null) {
            final DatabaseReference destinationReference = mDatabase.child("users").child(mAuth.getUid()).child("destinations");
            destinationReference.child(destinationName).removeValue();
        }
    }

    public void refreshTestActivity() {
        startActivity(new Intent(TestActivity.this, TestActivity.class));
        finish();
    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(TestActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSignOut() {
        signOut();
    }

    @Override
    public void onDeleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(TestActivity.this, "Your profile has been deleted", Toast.LENGTH_SHORT).show();
                        signOut();
                    } else {
                        Toast.makeText(TestActivity.this, "Failed to delete your account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onEditAccount(String newName, final String newEmail, String newPassword) {
        if (firebaseUser != null) {
            final DatabaseReference userReference = mDatabase.child("users").child(mAuth.getUid());

            if (!TextUtils.isEmpty(newEmail)) {
                firebaseUser.updateEmail(newEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(TestActivity.this, "Failed to update email", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "Error updating email");
                                } else {
                                    Toast.makeText(TestActivity.this, "Email updated", Toast.LENGTH_LONG).show();

                                    try {
                                        Map<String, Object> updatedEmail = new HashMap<>();
                                        updatedEmail.put("email", newEmail);
                                        userReference.updateChildren(updatedEmail);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                }
                            }
                        });
            }

            if (!TextUtils.isEmpty(newPassword)) {
                firebaseUser.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(TestActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error updating password");
                                } else {
                                    Toast.makeText(TestActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            if (!TextUtils.isEmpty(newName)) {
                try {
                    Map<String, Object> updatedName = new HashMap<>();
                    updatedName.put("name", newName);
                    userReference.updateChildren(updatedName);
                    Toast.makeText(TestActivity.this, "Name updated", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(TestActivity.this, "Error updating name", Toast.LENGTH_SHORT).show();
                }
            }

        }

        // Restart activity
        refreshTestActivity();
    }
}
