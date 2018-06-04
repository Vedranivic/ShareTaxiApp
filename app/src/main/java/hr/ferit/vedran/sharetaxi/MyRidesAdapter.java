package hr.ferit.vedran.sharetaxi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

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
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tvFrom.setText(rides.get(position).getFrom());
        holder.tvTo.setText(rides.get(position).getTo());
    }
    @Override
    public int getItemCount() {
        return this.rides.size();
    }
}
