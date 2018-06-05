package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        ButterKnife.bind(this, view);
        etDate.setInputType(InputType.TYPE_NULL);

        return view;
    }

    @OnClick(R.id.btCreateNew)
    public void onCreateButtonClick(View v){
        createNewRide();
    }

    @OnClick(R.id.etNewDate)
    public void onEditBoxClick(View v){
        showDatePicker(); Log.e("CLICK","SHOWED ON CLICK");
    }

    @OnFocusChange(R.id.etNewDate)
    public void onEditBoxFocusChange(View v){
        showDatePicker(); Log.e("FOCUS","SHOWN ON FOCUS");
    }

    private void createNewRide(){
        etFrom.setText("NISTAAA");
    }

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

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            etDate.setText(String.format("%s/%s/%s",String.valueOf(dayOfMonth),String.valueOf(monthOfYear+1),String.valueOf(year)));
        }
    };

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
            return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        }
    }
}
