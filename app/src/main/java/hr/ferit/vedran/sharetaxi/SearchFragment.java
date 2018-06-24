package hr.ferit.vedran.sharetaxi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindFont;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vedra on 4.6.2018..
 */

public class SearchFragment extends Fragment{

    @BindView(R.id.btSearch)
    Button btSearch;
    @BindView(R.id.etSearchFrom)
    EditText etFrom;
    @BindView(R.id.etSearchTo)
    EditText etTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.btSearch)
    public void showResults(){
        Bundle bundle = new Bundle();
        bundle.putString("from",etFrom.getText().toString());
        bundle.putString("to",etTo.getText().toString());
        Fragment resultFragment = new ResultsFragment();
        resultFragment.setArguments(bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.results_container, resultFragment)
                .commit();
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etFrom.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etTo.getWindowToken(), 0);
    }

}
