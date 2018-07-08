package hr.ferit.vedran.sharetaxi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.ferit.vedran.sharetaxi.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.rvChat)
    RecyclerView rvChat;
    @BindView(R.id.btSend)
    ImageView ivSend;
    @BindView(R.id.etMessage)
    EditText etMessage;
    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        chatID = getIntent().getStringExtra("CHAT_ID");
        setTitle(getIntent().getStringExtra("TITLE"));
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getMessages();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(),ConversationsActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMessages() {
        final LinearLayoutManager chatLLM = new LinearLayoutManager(this);
        chatLLM.setOrientation(LinearLayoutManager.VERTICAL);
        chatLLM.setStackFromEnd(true);
        rvChat.setLayoutManager(chatLLM);
        DatabaseReference dbMessages = FirebaseDatabase.getInstance().getReference().child("Messages").child(chatID);
        final FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> messagesAdapter;
        messagesAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.message,
                MessageViewHolder.class,
                dbMessages) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                viewHolder.messageText.setText(model.getText());
                viewHolder.senderName.setText(model.getUserName());
                Date date = new Date(model.getTimestamp());
                @SuppressLint("SimpleDateFormat")
                Format format = new SimpleDateFormat("HH:mm dd/MM");
                viewHolder.messageTime.setText(format.format(date));
                if(model.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    viewHolder.container.setGravity(Gravity.END);
                    viewHolder.messageText.setTextColor(Color.WHITE);
                    viewHolder.senderName.setTextColor(0xAAFFFFFF);
                    viewHolder.messageTime.setTextColor(0xAAFFFFFF);
                    viewHolder.messageBackground.setBackground(
                            getResources().getDrawable(R.drawable.blue_message_shape));
                }
                else {
                    viewHolder.container.setGravity(Gravity.START);
                    viewHolder.messageText.setTextColor(Color.DKGRAY);
                    viewHolder.senderName.setTextColor(0xFF666666);
                    viewHolder.messageTime.setTextColor(0xFF666666);
                    viewHolder.messageBackground.setBackground(
                            getResources().getDrawable(R.drawable.white_shape));
                }
            }
        };
        
        //scroll rv to the last item added
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.e("ITEM COUNT:","____ = "+rvChat.getAdapter().getItemCount());
                chatLLM.scrollToPositionWithOffset(positionStart,0);
                //rvChat.smoothScrollToPosition(positionStart);
            }
        });
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
