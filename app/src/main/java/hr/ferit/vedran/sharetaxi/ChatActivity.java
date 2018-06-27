package hr.ferit.vedran.sharetaxi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.rvChat)
    RecyclerView rvChat;
    @BindView(R.id.btSend)
    ImageView ivSend;
    @BindView(R.id.etMessage)
    EditText etMessage;
    private String chatID;
    private LinearLayoutManager chatLLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
//        Fragment chat = new ChatFragment();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_chat_container,chat)
//                .commit();
        chatID = getIntent().getStringExtra("CHAT_ID");
        setTitle(getIntent().getStringExtra("TITLE"));
        getMessages();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void getMessages() {
        DatabaseReference dbMessages = FirebaseDatabase.getInstance().getReference().child("Messages").child(chatID);
        final FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> messagesAdapter;
        messagesAdapter = new FirebaseRecyclerAdapter<ChatMessage,
                MessageViewHolder>(ChatMessage.class,
                R.layout.message,
                MessageViewHolder.class,
                dbMessages) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                viewHolder.messageText.setText(model.getText());
                viewHolder.senderName.setText(model.getUserName());
                Date date = new Date(model.getTimestamp());
                Format format = new SimpleDateFormat("HH:mm dd/MM");
                viewHolder.messageTime.setText(format.format(date));
                if(model.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    viewHolder.container.setGravity(Gravity.END);
                }
                else {
                    viewHolder.container.setGravity(Gravity.START);
                }
            }
        };
        //scroll rv to the last item added
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvChat.smoothScrollToPosition(positionStart);
            }
        });
        chatLLM = new LinearLayoutManager(this);
        chatLLM.setStackFromEnd(true);
        rvChat.setLayoutManager(chatLLM);
        rvChat.setAdapter(messagesAdapter);
    }

    @OnClick(R.id.btSend)
    public void sendMessage(View v){
        if(!etMessage.getText().toString().isEmpty()) {
            DatabaseReference dbMessages = FirebaseDatabase.getInstance().getReference().child("Messages").child(chatID);
            String id = dbMessages.push().getKey();
            String text = etMessage.getText().toString();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ChatMessage message = new ChatMessage(id,text,user.getDisplayName(),user.getUid());
            dbMessages.child(id).setValue(message);
            etMessage.setText("");
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
        }
    }

}
