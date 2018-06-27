package hr.ferit.vedran.sharetaxi;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by vedra on 4.6.2018..
 */

public class HomeFragment extends Fragment {

    @BindView(R.id.rvMyRides) RecyclerView rvMyRides;
    @BindView(R.id.rvAcceptedRides) RecyclerView rvAcceptedRides;
    private DatabaseReference databaseReference;
    private List<Ride> myRides;
    private List<Ride> acceptedRides;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,view);

        LinearLayoutManager myRidesLinearLayoutManager = new LinearLayoutManager(getContext());
        rvMyRides.setLayoutManager(myRidesLinearLayoutManager);
        LinearLayoutManager acceptedRidesLinearLayoutManager = new LinearLayoutManager(getContext());
        rvAcceptedRides.setLayoutManager(acceptedRidesLinearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        getMyRides();
        getAcceptedRides();
        return view;
}

    private void getMyRides(){
        databaseReference.child("Rides").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRides = new ArrayList<Ride>();
                final String USER_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot rideSnap : dataSnapshot.getChildren()) {
                    if(rideSnap
                            .getValue(Ride.class)
                            .getOwnerId()
                            .equals(USER_ID)){
                        myRides.add(rideSnap.getValue(Ride.class));
                    }
                }
                MyRidesAdapter rvMyRidesAdapter = new MyRidesAdapter(getContext(), myRides);
                changeItemLayout(rvMyRidesAdapter, R.id.rvMyRides);
                rvMyRides.setAdapter(rvMyRidesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FETCH DATA ERROR","Error populating RecyclerView");
            }
        });
    }

    private void getAcceptedRides() {
        databaseReference.child("Rides").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acceptedRides = new ArrayList<Ride>();
                final String USER_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Ride ride;
                ArrayList<String> passengerList;
                for(DataSnapshot rideSnap : dataSnapshot.getChildren()) {
                    ride = rideSnap.getValue(Ride.class);
                    passengerList = ride.getPassengerList();
                    for(String userId : passengerList)
                        if(userId.equals(USER_ID)){
                            acceptedRides.add(rideSnap.getValue(Ride.class));
                        }
                }
                MyRidesAdapter rvAcceptedRidesAdapter = new MyRidesAdapter(getContext(), acceptedRides);
                changeItemLayout(rvAcceptedRidesAdapter, R.id.rvAcceptedRides );
                rvAcceptedRides.setAdapter(rvAcceptedRidesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FETCH DATA ERROR","Error populating RecyclerView");
            }
        });
    }

    private void changeItemLayout(MyRidesAdapter adapter, int rvID){
        if(rvID == R.id.rvMyRides) {
            //for My Ride
            adapter.ibDeleteVisibility = View.VISIBLE;
            adapter.ibEditVisibility = View.VISIBLE;
            adapter.ibAcceptVisibility = View.GONE;
            adapter.ibSendVisibility = View.GONE;
        }
        else{
            //for Accepted Ride
            adapter.ibDeleteVisibility = View.VISIBLE;
            adapter.ibSendVisibility = View.VISIBLE;
            adapter.ibEditVisibility = View.GONE;
            adapter.ibAcceptVisibility = View.GONE;
        }
    }

}
