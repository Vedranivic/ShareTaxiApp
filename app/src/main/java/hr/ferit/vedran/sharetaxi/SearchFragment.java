package hr.ferit.vedran.sharetaxi;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vedra on 4.6.2018..
 */

public class SearchFragment extends Fragment{

    private RecyclerView rvMyRides;
    private LinearLayoutManager myRidesLinearLayoutManager;
    private MyRidesAdapter rvMyRidesAdapter;
    private DatabaseReference databaseReference;
    private List<Ride> myRides;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        rvMyRides = (RecyclerView)view.findViewById(R.id.rvMyRides);
        myRidesLinearLayoutManager = new LinearLayoutManager(getContext());
        rvMyRides.setLayoutManager(myRidesLinearLayoutManager);

        myRides = new ArrayList<Ride>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rides");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //getAllRides(dataSnapshot);
                Ride ride = dataSnapshot.getValue(Ride.class);
                myRides.add(ride);

                rvMyRidesAdapter = new MyRidesAdapter(getContext(), myRides);
                rvMyRides.setAdapter(rvMyRidesAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
