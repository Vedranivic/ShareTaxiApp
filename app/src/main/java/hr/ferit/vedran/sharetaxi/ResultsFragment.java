package hr.ferit.vedran.sharetaxi;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vedra on 19.6.2018..
 */

public class ResultsFragment extends Fragment {

    @BindView(R.id.rvMatchingRides)
    RecyclerView rvMatchingRides;
    private DatabaseReference databaseReference;
    private List<Ride> matchingRides;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        ButterKnife.bind(this,view);
        LinearLayoutManager myRidesLinearLayoutManager = new LinearLayoutManager(getContext());
        rvMatchingRides.setLayoutManager(myRidesLinearLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getResults();

        return view;
    }

    private void getResults(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("Rides")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchingRides = new ArrayList<Ride>();
                String from = getArguments().getString("from");
                String to = getArguments().getString("to");
                Ride ride;
                for(DataSnapshot rideSnap : dataSnapshot.getChildren()) {
                    ride = rideSnap.getValue(Ride.class);
                    if(ride.getFrom().toLowerCase().contains(from.toLowerCase())
                            && ride.getTo().toLowerCase().contains(to.toLowerCase())
                            && !ride.getOwnerId().equals(user.getUid())
                            && Integer.parseInt(ride.getPassengers())<8
                            && !ride.getPassengerList().contains(user.getUid())) {
                        matchingRides.add(rideSnap.getValue(Ride.class));
                    }
                }
                MyRidesAdapter rvMatchingRidesAdapter = new MyRidesAdapter(getContext(), matchingRides);
                changeItemLayout(rvMatchingRidesAdapter);
                rvMatchingRides.setAdapter(rvMatchingRidesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FETCH DATA ERROR","Error populating RecyclerView");
            }
        });

    }

    private void changeItemLayout(MyRidesAdapter adapter) {
        adapter.ibDeleteVisibility = View.GONE;
        adapter.ibEditVisibility = View.GONE;
        adapter.ibAcceptVisibility = View.VISIBLE;
        adapter.ibSendVisibility = View.GONE;
    }
}
