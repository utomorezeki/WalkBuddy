package com.mobileapps.walkbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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
import com.mobileapps.walkbuddy.models.User;
import com.mobileapps.walkbuddy.walkbuddy.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FindRoutesFragment.OnFindRoutesFragmentInteractionListener, RoutesFragment.OnRoutesFragmentInteractionListener,
        AccountFragment.OnAccountFragmentInteractionListener, DestinationsFragment.OnDestinationsFragmentInteractionListener, HelpAboutFragment.OnHelpAboutFragmentInteractionListener, SaveRouteFragment.OnSaveRouteFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private NavigationView mNavigationView;

    private User user;
    public List<Destination> destinations = new ArrayList<>();
    private FirebaseUser firebaseUser;

    private ArrayList<Route> quickestRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();
        // Get Database instance
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = mAuth.getCurrentUser();

        if(getIntent().hasExtra("data"))
        {
            Bundle extras = getIntent().getBundleExtra("data");
            CharSequence destinationName = extras.getCharSequence("name");
            ArrayList<Double> userLat = (ArrayList<Double>) extras.getSerializable("userLat");
            ArrayList<Double> userLng = (ArrayList<Double>) extras.getSerializable("userLng");
            double destLat = extras.getDouble("destLat");
            double destLng = extras.getDouble("destLng");
            long timeInMillis = extras.getLong("timeInMillis");

            if (savedInstanceState == null) {
                Fragment fragment = SaveRouteFragment.newInstance(destinationName, userLat, userLng, destLat, destLng, timeInMillis);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();
            }
        } else {
            if (savedInstanceState == null) {
                // Quickest routes
                if(mAuth.getCurrentUser() != null) {
                    mDatabase.child("users").child(mAuth.getUid()).child("destinations").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    List<Destination> dests = new ArrayList<>();
                                    Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                                    for (DataSnapshot snap : it) {
                                        dests.add(snap.getValue(Destination.class));
                                    }
                                    quickestRoutes = getQuickestRoutes(dests);

                                    Fragment fragment = FindRoutesFragment.newInstance(quickestRoutes);

                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, databaseError.toString());
                                }
                            }
                    );
                }
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Update navbar info and add ValueEventListener to user to stay updated with current
        // database values.
        if(mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getUid()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);

                            View header = mNavigationView.getHeaderView(0);
                            TextView name = header.findViewById(R.id.navName);
                            TextView email = header.findViewById(R.id.navEmail);
                            name.setText(user.getName());
                            email.setText(user.getEmail());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.toString());
                        }
                    }
            );

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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        try {
            if (id == R.id.nav_routes) {
                fragment = FindRoutesFragment.newInstance(quickestRoutes);
            } else if (id == R.id.nav_destinations) {
                fragment = DestinationsFragment.newInstance(destinations);
            } else if (id == R.id.nav_account) {
                fragment = AccountFragment.class.newInstance();
            } else if (id == R.id.nav_help_and_about) {
                fragment = HelpAboutFragment.class.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSignOut() {
        signOut();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDeleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Your profile has been deleted", Toast.LENGTH_SHORT).show();
                        signOut();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to delete your account", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEditAccount(final String newName, final String newEmail, String newPassword) {
        if (firebaseUser != null) {
            final DatabaseReference userReference = mDatabase.child("users").child(mAuth.getUid());

            if (!TextUtils.isEmpty(newEmail)) {
                firebaseUser.updateEmail(newEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Failed to update email", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "Error updating email");
                                } else {
                                    Toast.makeText(MainActivity.this, "Email updated", Toast.LENGTH_LONG).show();

                                    User updatedUser = new User(user.getName(), newEmail);
                                    try {
                                        userReference.setValue(updatedUser);
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
                                    Toast.makeText(MainActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error updating password");
                                } else {
                                    Toast.makeText(MainActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            if (!TextUtils.isEmpty(newName)) {
                User updatedUser = new User(newName, user.getEmail());
                try {
                    userReference.setValue(updatedUser);
                    Toast.makeText(MainActivity.this, "Name updated", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(MainActivity.this, "Error updating name", Toast.LENGTH_SHORT).show();
                }
            }

        }

        // Restart activity
        refreshMainActivity();
    }

    public void refreshMainActivity() {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart called");
    }

    @Override
    public void onFindRoutesFragmentInteraction() {
        Log.i(TAG, "onFindRoutesFragmentInteraction");
    }

    @Override
    public void onHelpAboutFragmentInteraction(Uri uri) {
        Log.i(TAG, "onHelpAboutFragmentInteraction");
    }

    @Override
    public void deleteDestination(String destinationName) {
        if (firebaseUser != null) {
            final DatabaseReference destinationReference = mDatabase.child("users").child(mAuth.getUid()).child("destinations");
            String trimmedName = destinationName;
            trimmedName = trimmedName.replaceAll("\\P{Print}", "");
            trimmedName = trimmedName.replaceAll("\\.|\\$|\\[|\\]|#|/", "");
            destinationReference.child(trimmedName).removeValue();
        }
    }

    @Override
    public void cancelSaveRoute() {
        Toast.makeText(MainActivity.this, "Route cancelled", Toast.LENGTH_SHORT).show();
        refreshMainActivity();
    }

    @Override
    public void saveRoute(String destinationName, String startLocationName, List<Double> userLat, List<Double> userLng, long timeInMillis) {
        List<Route> routes = null;
        Route newRoute = new Route(destinationName, startLocationName, timeInMillis, userLat, userLng);
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

            Toast.makeText(MainActivity.this, "Your route has been saved", Toast.LENGTH_SHORT).show();
        }

        refreshMainActivity();
    }

    @Override
    public void deleteRoute(String destinationName, int routeIndex) {
        if (firebaseUser != null) {
            final DatabaseReference destinationReference = mDatabase.child("users").child(mAuth.getUid()).child("destinations");
            Destination destinationToPut = null;

            for (Destination d : destinations) {
                if (d.getDestinationName().equals(destinationName)) {
                    destinationToPut = d;
                }
            }

            List<Route> routes = destinationToPut.getRoutes();
            routes.remove(routeIndex);

            destinationToPut = new Destination(routes, destinationName);
            String trimmedName = destinationName;
            trimmedName = trimmedName.replaceAll("\\P{Print}", "");
            trimmedName = trimmedName.replaceAll("\\.|\\$|\\[|\\]|#|/", "");

            Map<String, Object> updatedDestination = new HashMap<>();
            updatedDestination.put(trimmedName, destinationToPut);
            destinationReference.updateChildren(updatedDestination);
        }
    }

    public ArrayList<Route> getQuickestRoutes(List<Destination> destinations) {
        ArrayList<Route> result = new ArrayList<>();
        for(Destination d : destinations) {
            List<Route> routes = d.getRoutes();
            for(Route r : routes) {
                result.add(r);
            }
        }
        RouteTimeComparator comparator = new RouteTimeComparator();
        Collections.sort(result, comparator);

        return result;
    }
}
