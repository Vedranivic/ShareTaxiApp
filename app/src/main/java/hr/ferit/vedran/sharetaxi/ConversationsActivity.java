package hr.ferit.vedran.sharetaxi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.ferit.vedran.sharetaxi.model.Chat;

public class ConversationsActivity extends AppCompatActivity {

    @BindView(R.id.rvMyConversations)
    RecyclerView rvMyChats;
    List<Chat> myChats;
    MyChatsAdapter myChatsAdapter;
    DatabaseReference dbChats;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        ButterKnife.bind(this);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getMyChats();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(),MyRidesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMyChats() {
        LinearLayoutManager chatsLLM = new LinearLayoutManager(this);
        rvMyChats.setLayoutManager(chatsLLM);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbChats = FirebaseDatabase.getInstance().getReference().child("Chats");

        dbChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myChats = new ArrayList<>();
                for(DataSnapshot chatSnap : dataSnapshot.getChildren()){
                    Chat chat = chatSnap.getValue(Chat.class);
                    if(chat.getUser1id().equals(user.getUid()) || chat.getUser2id().equals(user.getUid())){
                        myChats.add(chat);
                    }
                }
                myChatsAdapter = new MyChatsAdapter(getApplicationContext(),myChats);
                rvMyChats.setAdapter(myChatsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR_CHATS","Error fetching chats");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myChatsAdapter!=null) {
            myChatsAdapter.notifyDataSetChanged();
        }
    }

}
