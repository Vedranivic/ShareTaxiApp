package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.TimeZone;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLDisplay;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Optional;


/**
 * Created by vedra on 4.6.2018..
 */

public class CreateFragment extends Fragment{

    @BindView(R.id.etNewFrom) EditText etFrom;
    @BindView(R.id.etNewTo) EditText etTo;
    @BindView(R.id.etNewPassengers) EditText etPassengers;
    @BindView(R.id.etNewDate) EditText etDate;
    @BindView(R.id.etNewTime) EditText etTime;
    @BindView(R.id.btCreateNew) Button btCreate;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        ButterKnife.bind(this, view);
        etDate.setInputType(InputType.TYPE_NULL);
        context = getActivity().getApplicationContext();

        return view;
    }

    @OnClick(R.id.btCreateNew)
    public void onCreateButtonClick(View v){
        createNewRide();
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
                Fragment returnHome = new HomeFragment();
                if (returnHome != null) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, returnHome)
                            .commit();
                }
                BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNav);
                navigation.setSelectedItemId(R.id.navigation_home);
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
                    Toast.makeText(getContext(), "Number of passengers exceeds maximal number of passengers per ride", Toast.LENGTH_SHORT).show();
                    inputOK = false;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number of passengers", Toast.LENGTH_SHORT).show();
            inputOK = false;
        }
        //Check date and time
        try {
            if (inputOK) {
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
}
