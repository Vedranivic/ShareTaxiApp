package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import hr.ferit.vedran.sharetaxi.model.Ride;


public class CreateFragment extends Fragment{

    @BindView(R.id.CreateNewRideText) TextView tvAction;
    @BindView(R.id.tvCancel) TextView tvCancel;
    @BindView(R.id.etNewFrom) EditText etFrom;
    @BindView(R.id.etNewTo) EditText etTo;
    @BindView(R.id.etNewPassengers) EditText etPassengers;
    @BindView(R.id.etNewDate) EditText etDate;
    @BindView(R.id.etNewTime) EditText etTime;
    @BindView(R.id.btCreateNew) Button btCreate;
    @BindView(R.id.btUseMyLocationFrom) Button btLocateFrom;
    @BindView(R.id.btUseMyLocationTo) Button btLocateTo;
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
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        ButterKnife.bind(this, view);
        etDate.setInputType(InputType.TYPE_NULL);
        context = getActivity().getApplicationContext();

        if(getArguments()!=null) {
            editingMode = true;
            startEditingRide(getArguments().getString("Ride_ID"));
        }
        else {
            editingMode = false;
        }
        return view;
    }

    private void startEditingRide(final String RIDE_ID) {
        tvAction.setText(R.string.editRideText);
        btCreate.setText(getResources().getString(R.string.fui_button_text_save));
        tvCancel.setVisibility(View.VISIBLE);
        dbRides = FirebaseDatabase.getInstance().getReference().child("Rides").child(RIDE_ID);
        dbRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rideToEdit = dataSnapshot.getValue(Ride.class);
                etFrom.setText(rideToEdit.getFrom());
                etTo.setText(rideToEdit.getTo());
                etPassengers.setText(rideToEdit.getPassengers());
                etDate.setText(rideToEdit.getDate());
                etTime.setText(rideToEdit.getTime());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR_RIDE:EDIT","Error fetching ride's data for editing");
            }
        });
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

    @OnClick(R.id.btCreateNew)
    public void onCreateButtonClick(View v){
        if(editingMode){
            editRide();
        }
        else {
            createNewRide();
        }
    }

    @OnClick(R.id.tvCancel)
    public void onCancelButtonClick(View v){
        returnHome();
    }

    private void editRide() {
        String from = etFrom.getText().toString();
        String to = etTo.getText().toString();
        String passengers = etPassengers.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        if(validateInput(from, to, passengers, date, time)){
            rideToEdit.setFrom(from);
            rideToEdit.setTo(to);
            rideToEdit.setPassengers(passengers);
            rideToEdit.setDate(date);
            rideToEdit.setTime(time);
            dbRides.setValue(rideToEdit);
            returnHome();
            Toast.makeText(context,"Your ride has been updated",Toast.LENGTH_SHORT).show();
        }

    }

    //Methods for catching the date box click
    @OnClick(R.id.etNewDate)
    public void onEditBoxClick(View v){
        showDatePicker();
    }
    @OnFocusChange(R.id.etNewDate)
    public void onEditBoxFocusChange(View v){
        showDatePicker();
    }

    //Validating input and creating new ride (adding to the database)
    private void createNewRide(){
        String from = etFrom.getText().toString();
        String to = etTo.getText().toString();
        String passengers = etPassengers.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        FirebaseUser owner = FirebaseAuth.getInstance().getCurrentUser();

        if(validateInput(from, to, passengers, date, time)){
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference();
            try{
                String id = databaseReference.child("Rides").push().getKey();
                Ride ride = new Ride(id,from,to,passengers,date,time, owner.getUid(),owner.getDisplayName(),null);
                databaseReference.child("Rides").child(id).setValue(ride);
                returnHome();
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Your ride has been posted!");
                alertDialog.setMessage("Wait for someone to accept your ride.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                //Toast.makeText(getContext(),"Your ride has been posted.",Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(getContext(),"Data could not be written. Try again later.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String from, String to, String passengers, String date, String time){
        boolean inputOK = true;

        //Check all fields entered
        if(from.isEmpty()|| to.isEmpty()|| passengers.isEmpty()|| date.isEmpty()|| time.isEmpty()){
            Toast.makeText(getContext(),"Please enter value for all fields", Toast.LENGTH_SHORT).show();
            inputOK = false;
        }
        //Check address differing
        if (from.equals(to) && inputOK) {
            Toast.makeText(getContext(), "'From' address and 'To' address should differ", Toast.LENGTH_SHORT).show();
            inputOK = false;
        }
        //Check number of passengers
        try {
            if (inputOK) {
                int passengersNumber = Integer.parseInt(passengers);
                if (passengersNumber >= 9) {
                    Toast.makeText(getContext(), "Number of passengers exceeds maximal number of passengers per ride", Toast.LENGTH_LONG).show();
                    inputOK = false;
                }
                if (passengersNumber < 1){
                    Toast.makeText(getContext(), "There has to be at least one passenger (owner) for a ride", Toast.LENGTH_LONG).show();
                    inputOK = false;
                }
                if (rideToEdit!=null){
                    int acceptedNumber = rideToEdit.getPassengerList().size();
                    if(passengersNumber < acceptedNumber + 1){
                        Toast.makeText(getContext(), "There has to be at least one passenger (owner) for a ride, along with the passengers who accepted the ride", Toast.LENGTH_LONG).show();
                        inputOK = false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number of passengers", Toast.LENGTH_SHORT).show();
            inputOK = false;
        }
        //Check date and time
        try {
            if (inputOK) {
                @SuppressLint("SimpleDateFormat")
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                Date dateValue = formatter.parse(date + " " + time);
                java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Zagreb"));
                Calendar calCurrent = Calendar.getInstance();
                Calendar calChosen = Calendar.getInstance();

                calChosen.setTime(dateValue);

                if (calChosen.before(calCurrent)) {
                    Toast.makeText(getContext(), "Please enter a valid time (non-past value)", Toast.LENGTH_SHORT).show();
                    inputOK = false;
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),"Please enter time in valid format (hh:mm)",Toast.LENGTH_SHORT).show();
            inputOK = false;
        }
        return inputOK;
    }

    //Displaying Fragment with DatePicker for setting the date value
    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        datePickerFragment.setArguments(args);

        datePickerFragment.setCallBack(ondate);
        datePickerFragment.show(getFragmentManager(), "Date Picker");
    }

    //Fragment class for Date Picker
    public static class DatePickerFragment extends DialogFragment {
        DatePickerDialog.OnDateSetListener ondateSet;
        private int year, month, day;

        public DatePickerFragment() {}

        public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
            ondateSet = ondate;
        }

        @SuppressLint("NewApi")
        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),ondateSet,year,month,day);
            //disabling past dates
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            return datePickerDialog;
        }
    }

    //Listener for detecting the setting of the date
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if(view.isShown()){
                etDate.setText(String.format("%s/%s/%s",String.valueOf(dayOfMonth),String.valueOf(monthOfYear+1),String.valueOf(year).substring(2)));
            }
        }
    };

    private void returnHome() {
        Fragment returnHome = new HomeFragment();
        if (returnHome != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, returnHome)
                    .commit();
        }
        BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNav);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

}
