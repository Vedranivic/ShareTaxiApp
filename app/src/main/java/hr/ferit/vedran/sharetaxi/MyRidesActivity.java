package hr.ferit.vedran.sharetaxi;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRidesActivity extends AppCompatActivity {

    @BindView(R.id.rvMyRides)
    RecyclerView rvMyRides;
    private LinearLayoutManager myRidesLinearLayoutManager;
    private MyRidesAdapter rvMyRidesAdapter;
    private DatabaseReference databaseReference;
    private List<Ride> myRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
        ButterKnife.bind(this);

        myRides = new ArrayList<Ride>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rides");
        myRidesLinearLayoutManager = new LinearLayoutManager(this);
        rvMyRides.setLayoutManager(myRidesLinearLayoutManager);

/*        String addressFrom = "Zupanijska 4";
        String addressTo = "Istarska 5";
        Ride ride = new Ride(addressFrom, addressTo);
        databaseReference.push().setValue(ride);*/

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //getAllRides(dataSnapshot);
                Ride ride = dataSnapshot.getValue(Ride.class);
                myRides.add(ride);
                rvMyRidesAdapter = new MyRidesAdapter(MyRidesActivity.this, myRides);
                rvMyRides.setAdapter(rvMyRidesAdapter);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //getAllRides(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                deleteRide(dataSnapshot);
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllRides(DataSnapshot dataSnapshot) {
        for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
            String from = rideSnapshot.getValue(String.class);
            String to = rideSnapshot.getValue(String.class);
            myRides.add(new Ride(from,to));
            rvMyRidesAdapter = new MyRidesAdapter(MyRidesActivity.this, myRides);
            rvMyRides.setAdapter(rvMyRidesAdapter);
        }
    }
    private void deleteRide(DataSnapshot dataSnapshot){

    }

}
