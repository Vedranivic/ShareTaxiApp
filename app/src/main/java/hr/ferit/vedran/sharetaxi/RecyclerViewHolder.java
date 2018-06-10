package hr.ferit.vedran.sharetaxi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vedra on 3.6.2018..
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvFrom) TextView tvFrom;
    @BindView(R.id.tvTo) TextView tvTo;
    @BindView(R.id.tvPassengers) TextView tvPassengers;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.ibDeleteMyRide) ImageButton ibDeleteMyRide;

    private List<Ride> rides;

    public RecyclerViewHolder (final View itemView, final List<Ride> rides){
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.rides = rides;
    }
}
