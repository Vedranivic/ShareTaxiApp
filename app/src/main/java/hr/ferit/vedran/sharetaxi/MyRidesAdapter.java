package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

import hr.ferit.vedran.sharetaxi.model.Chat;
import hr.ferit.vedran.sharetaxi.model.Ride;


public class MyRidesAdapter extends RecyclerView.Adapter<RideViewHolder> {
    private List<Ride> rides;
    private Context context;
    int ibEditVisibility;
    int ibDeleteVisibility;
    int ibAcceptVisibility;
    int ibSendVisibility;
    private String chatID;
    private RecyclerView rv;

    MyRidesAdapter(Context context, List<Ride> rides) {
        this.rides = rides;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = recyclerView;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        RideViewHolder viewHolder;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride, parent, false);
        viewHolder = new RideViewHolder(layoutView, rides);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        final int pos = position;
        final Ride ride = rides.get(position);
        holder.tvFrom.setText(ride.getFrom());
        holder.tvTo.setText(ride.getTo());
        holder.tvTime.setText(ride.getTime());
        holder.tvPassengers.setText(ride.getPassengers());
        holder.tvOwnerName.setText(ride.getOwnerName().split(" (?!.* )")[0]);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Calendar calToday = Calendar.getInstance();
        Calendar calRide = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
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

        holder.ibEditRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRide(ride);
            }
        });

        holder.ibDeleteRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rv.getId()==R.id.rvMyRides) {
                    //for My Ride
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Warning");
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
                                    deleteMyRide(ride, pos);
                                }
                            });
                    alertDialog.show();
                }
                else{
                    //for Accepted Ride
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Are you sure you want to cancel this accepted ride?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
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
                                    cancelRide(ride, user.getUid());
                                }
                            });
                    alertDialog.show();
                }


            }
        });


        holder.ibAcceptRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRide(ride,user.getUid());
                FragmentActivity activity = (FragmentActivity) context;
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new HomeFragment())
                        .commit();
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Ride has been accepted!");
                alertDialog.setMessage("You have accepted a ride.Contact the ride's owner for details." +
                        "\n\n(You can send the owner a direct message on a click of a button in your accepted ride)");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        holder.ibSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(ride,user.getUid());
            }
        });

        holder.ibAcceptRide.setVisibility(ibAcceptVisibility);
        holder.ibEditRide.setVisibility(ibEditVisibility);
        holder.ibDeleteRide.setVisibility(ibDeleteVisibility);
        holder.ibSendMessage.setVisibility(ibSendVisibility);
    }

    private void editRide(Ride ride){
        FragmentActivity activity = (FragmentActivity)context;
        Bundle bundle = new Bundle();
        bundle.putString("Ride_ID",ride.getId());
        Fragment createFragment = new CreateFragment();
        createFragment.setArguments(bundle);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, createFragment)
                .commit();
    }

    private void cancelRide(Ride ride, String USER_ID) {
        ride.removePassenger(USER_ID);
        FirebaseDatabase.getInstance().getReference().child("Rides")
                .child(ride.getId()).setValue(ride);
    }

    private void acceptRide(Ride ride, String USER_ID) {
        ride.addPassenger(USER_ID);
        FirebaseDatabase.getInstance().getReference()
                .child("Rides").child(ride.getId()).setValue(ride);
    }

    private void sendMessage(final Ride ride, String USER_ID) {
        final String user1id = USER_ID;
        final String user2id = ride.getOwnerId();
        final DatabaseReference dbChats = FirebaseDatabase.getInstance().getReference().child("Chats");
        dbChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean chatExists = false;
                for (DataSnapshot chatSnap : dataSnapshot.getChildren()) {
                    Chat chat = chatSnap.getValue(Chat.class);
                    if((chat.getUser1id().equals(user1id) && chat.getUser2id().equals(user2id))
                            ||(chat.getUser1id().equals(user2id) && chat.getUser2id().equals(user1id))){
                        chatID = chat.getId();
                        chatExists = true;
                        break;
                    }
                }
                if(!chatExists) {
                    chatID = dbChats.push().getKey();
                    Chat chat = new Chat(chatID, user1id, user2id);
                    dbChats.child(chatID).setValue(chat);
                }
                Intent conversation = new Intent(context,ChatActivity.class);
                conversation.putExtra("CHAT_ID",chatID);
                conversation.putExtra("TITLE",ride.getOwnerName());
                context.startActivity(conversation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FETCH DATA ERROR", "Error fetching chats");
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
                            //remove item from list and refresh recyclerview
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
