package hr.ferit.vedran.sharetaxi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

/**
 * Created by vedra on 26.6.2018..
 */

public class ConversationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations,container,false);
        ButterKnife.bind(this,view);
        getActivity().setTitle("My Chats");
        return view;
    }
}
