package hr.ferit.vedran.sharetaxi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vedra on 3.6.2018..
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvFrom) TextView tvFrom;
    @BindView(R.id.tvTo) TextView tvTo;
    private List<Ride> rides;

    public RecyclerViewHolder (final View itemView, final List<Ride> rides){
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.rides = rides;
    }
}
