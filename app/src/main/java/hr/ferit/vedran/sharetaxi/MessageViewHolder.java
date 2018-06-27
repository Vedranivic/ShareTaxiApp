package hr.ferit.vedran.sharetaxi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vedra on 26.6.2018..
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvMessageText) TextView messageText;
    @BindView(R.id.tvSender) TextView senderName;
    @BindView(R.id.tvTime) TextView messageTime;
    @BindView(R.id.messageContainerLayout) LinearLayout container;

    public MessageViewHolder(View v) {
        super(v);
        ButterKnife.bind(this,v);
    }
}
