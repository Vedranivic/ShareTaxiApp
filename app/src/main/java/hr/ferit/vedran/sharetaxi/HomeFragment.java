package hr.ferit.vedran.sharetaxi;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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
    private MyRidesAdapter rvMyRidesAdapter;
    private DatabaseReference databaseReference;
    private List<Ride> myRides;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,view);
        LinearLayoutManager myRidesLinearLayoutManager = new LinearLayoutManager(getContext());
        rvMyRides.setLayoutManager(myRidesLinearLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getMyRides();

        return view;
}

    private void getMyRides(){
        databaseReference.child("Rides").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRides = new ArrayList<Ride>();
                for(DataSnapshot rideSnap : dataSnapshot.getChildren()) {
                    myRides.add(rideSnap.getValue(Ride.class));
                }
                MyRidesAdapter rvMyRidesAdapter = new MyRidesAdapter(getContext(), myRides);
                rvMyRides.setAdapter(rvMyRidesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FETCH DATA ERROR","Error populating RecyclerView");
            }
        });
    }

}
