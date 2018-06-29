package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hr.ferit.vedran.sharetaxi.model.Chat;
import hr.ferit.vedran.sharetaxi.model.ChatMessage;
import hr.ferit.vedran.sharetaxi.model.User;

public class MyChatsAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private List<Chat> chats;
    private Context context;

    MyChatsAdapter(Context context, List<Chat> chats) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatViewHolder viewHolder;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation, parent, false);
        viewHolder = new ChatViewHolder(layoutView, chats);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position) {
        Chat chat = chats.get(position);
        final String chatID = chat.getId();
        final String user1Id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String user2Id;
        if(chat.getUser1id().equals(user1Id)) {
            user2Id = chat.getUser2id();
        }
        else {
            user2Id = chat.getUser1id();
        }
        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user2Id);
        Query dbMessage = FirebaseDatabase.getInstance().getReference()
                .child("Messages").child(chatID).orderByKey().limitToLast(1);

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.tvUserName.setText(dataSnapshot.getValue(User.class).getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR_Users","Error fetching User data");
            }
        });

        dbMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressLint("SimpleDateFormat")
                Format format = new SimpleDateFormat("HH:mm dd/MM");
                Date date;
                for(DataSnapshot msgSnap : dataSnapshot.getChildren()){
                    ChatMessage message = msgSnap.getValue(ChatMessage.class);
                    String last = message.getText();
                    if(message.getUserId().equals(user1Id)) {
                        last = "You: " + last;
                    }
                    if(last.length()>45){
                        last = last.substring(0,44)+"...";
                    }
                    holder.tvLastMessage.setText(last);
                    date = new Date(message.getTimestamp());
                    holder.tvLastTime.setText(format.format(date));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR_Messages","Error fetching last message");
            }
        });

        holder.ibStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent conversation = new Intent(context,ChatActivity.class);
                conversation.putExtra("CHAT_ID",chatID);
                conversation.putExtra("TITLE",holder.tvUserName.getText().toString());
                context.startActivity(conversation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.chats.size();
    }
}
