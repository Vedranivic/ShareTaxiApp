package hr.ferit.vedran.sharetaxi;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by vedra on 3.6.2018..
 */

public class MyRidesAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<Ride> rides;
    protected Context context;

    public MyRidesAdapter(Context context, List<Ride> rides) {
        this.rides = rides;
        this.context = context;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        RecyclerViewHolder viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride, parent, false);
        viewHolder = new RecyclerViewHolder(layoutView, rides);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Ride ride = rides.get(position);
        holder.tvFrom.setText(ride.getFrom());
        holder.tvTo.setText(ride.getTo());
        holder.tvTime.setText(ride.getTime());
        holder.tvPassengers.setText(ride.getPassengers());

        Calendar calToday = Calendar.getInstance();
        Calendar calRide = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date dateValue = formatter.parse(ride.getDate());
            calRide.setTime(dateValue);
        }
        catch (Exception e){
            Log.e("ADAPTER: DATE","Date parse error!");
        }
        if(calRide.after(calToday)){
            holder.tvDate.setText(ride.getDate());
        }

        holder.ibDeleteMyRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to delete this ride?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteMyRide(ride, position);
                            }
                        });
                alertDialog.show();

            }
        });
    }

    private void deleteMyRide(Ride ride, final int position){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Rides").child(ride.getId()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //remove item from list alos and refresh recyclerview
                            rides.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, rides.size());

                            Toast.makeText(context,
                                    "Your ride has been deleted.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,
                                    "Your ride cannot be deleted at the moment.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return this.rides.size();
    }
}
