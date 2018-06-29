package hr.ferit.vedran.sharetaxi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.ferit.vedran.sharetaxi.model.Chat;


class ChatViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvLastMessage)
    TextView tvLastMessage;
    @BindView(R.id.tvLastTime)
    TextView tvLastTime;
    @BindView(R.id.ibStartChat)
    ImageButton ibStartChat;
    private List<Chat> chats;

    ChatViewHolder(final View itemView, final List<Chat> chats){
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.chats = chats;
    }
}
