package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hr.ferit.vedran.sharetaxi.model.Ride;
import hr.ferit.vedran.sharetaxi.model.User;

public class MyRidesActivity extends AppCompatActivity
                             implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 995;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    SharedPreferences preferences;
    public static String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        //setup bottom navigation
        BottomNavigationView navigation = findViewById(R.id.bottomNav);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(this);

        preferences = this.getSharedPreferences("com.sharetaxi",MODE_PRIVATE);
        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btChat) {
            startActivity(new Intent(getApplicationContext(),ConversationsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_search:
                fragment = new SearchFragment();
                break;

            case R.id.navigation_create:
                fragment = new CreateFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private void login(){
        if(!preferences.contains("UserID")) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.taxi_logo)
                            .setTheme(R.style.SignInTheme)
                            .build(),
                    RC_SIGN_IN);
        }
        else{
            loadFragment(new HomeFragment());
            Toast.makeText(this,"Welcome back, "+FirebaseAuth.getInstance().getCurrentUser()
                    .getDisplayName().split(" (?!.* )")[0],Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    preferences.edit().putString("UserID",user.getUid()).apply();
                    USER_ID = user.getUid();

                    DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                    User dbUser = new User(user.getUid(),user.getEmail(),user.getDisplayName(),user.getProviders().get(0));
                    dbUsers.child(USER_ID).setValue(dbUser);

                    loadFragment(new HomeFragment());
                    Toast.makeText(this,"Welcome, "+user.getDisplayName().split(" (?!.* )")[0],Toast.LENGTH_SHORT).show();

                }



            }
            else{
                Toast.makeText(this, "Error: Sign in failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeOldRides() {
        final DatabaseReference dbRides = FirebaseDatabase.getInstance().getReference().child("Rides");
        dbRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressLint("SimpleDateFormat")
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                Date dateValue;
                java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Zagreb"));
                Calendar calCurrent;
                Calendar calRide;
                for(DataSnapshot rideSnap : dataSnapshot.getChildren()){
                    Ride ride = rideSnap.getValue(Ride.class);
                    try {
                        dateValue = formatter.parse(ride.getDate() + " " + ride.getTime());
                        calCurrent = Calendar.getInstance();
                        calRide = Calendar.getInstance();
                        calRide.setTime(dateValue);
                        if (calCurrent.before(calRide)) {
                            Log.e("CURRENT",calCurrent.toString());
                            Log.e("RIDECAL",calRide.toString());
                            //dbRides.child(ride.getId()).removeValue();
                        }
                    }
                    catch (Exception e){
                        Log.e("DATETIME PARSE ERROR","Error while parsing date and time");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR_REMOVING","Error while removing old rides");
            }
        });
    }

}
