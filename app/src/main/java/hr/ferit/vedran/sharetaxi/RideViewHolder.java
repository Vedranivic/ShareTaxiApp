package hr.ferit.vedran.sharetaxi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.ferit.vedran.sharetaxi.model.Ride;


class RideViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvFrom) TextView tvFrom;
    @BindView(R.id.tvTo) TextView tvTo;
    @BindView(R.id.tvPassengers) TextView tvPassengers;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.ibDeleteRide) ImageButton ibDeleteRide;
    @BindView(R.id.ibEditRide) ImageButton ibEditRide;
    @BindView(R.id.ibAcceptRide) ImageButton ibAcceptRide;
    @BindView(R.id.tvOwnerName) TextView tvOwnerName;
    @BindView(R.id.ibSendMessage) ImageButton ibSendMessage;
    private List<Ride> rides;

    RideViewHolder(final View itemView, final List<Ride> rides){
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.rides = rides;
    }

}
