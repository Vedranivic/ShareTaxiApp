package hr.ferit.vedran.sharetaxi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.ferit.vedran.sharetaxi.model.Ride;

public class SearchFragment extends Fragment{

    @BindView(R.id.btSearch)
    Button btSearch;
    @BindView(R.id.etSearchFrom)
    EditText etFrom;
    @BindView(R.id.etSearchTo)
    EditText etTo;
    private Context context;
    private FusedLocationProviderClient fusedLPC;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location myLocation;
    private LocationCallback locationCallback;
    private Boolean LocationForFrom;
    private DatabaseReference dbRides;
    private Ride rideToEdit;
    private Boolean editingMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);
        ButterKnife.bind(this,view);
        context = getActivity().getApplicationContext();
        return view;
    }

    @OnClick(R.id.btSearch)
    public void showResults(){
        Bundle bundle = new Bundle();
        bundle.putString("from",etFrom.getText().toString());
        bundle.putString("to",etTo.getText().toString());
        Fragment resultFragment = new ResultsFragment();
        resultFragment.setArguments(bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.results_container, resultFragment)
                .commit();
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etFrom.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etTo.getWindowToken(), 0);
    }

    @OnClick({R.id.btUseMyLocationFrom,R.id.btUseMyLocationTo})
    public void useMyLocation(final Button bt){
        switch (bt.getId()){
            case R.id.btUseMyLocationFrom:
                LocationForFrom = true; break;
            case R.id.btUseMyLocationTo:
                LocationForFrom = false; break;
        }
        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLPC = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                myLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };
        getLocation();
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if(fusedLPC!=null) {
                fusedLPC.requestLocationUpdates(locationRequest,
                        locationCallback,
                        null);
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(context,
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, MyLocationIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", myLocation);
        context.startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(context, "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }

            if (resultCode == 1) {
                Toast.makeText(context,
                        "Address not found, " ,
                        Toast.LENGTH_SHORT).show();
            }

            String address = resultData.getString("address_result");

            if(LocationForFrom) {
                etFrom.setText(address);
            }
            else {
                etTo.setText(address);
            }
        }
    }

}
