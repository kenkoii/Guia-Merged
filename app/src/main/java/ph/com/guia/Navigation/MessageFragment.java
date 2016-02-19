package ph.com.guia.Navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.Model.MessageItem;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class MessageFragment extends Fragment {
    //TODO: Kentoy
    ArrayList<MessageItem> mList = new ArrayList<MessageItem>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            LoggedInGuide.mToolbar.setTitle("Messages");
        }catch (Exception e){
            LoggedInTraveler.mToolbar.setTitle("Messages");
        }

        String message = "Hooyyy asa naman tawn ka ui, pagdali kay ikaw nalay gihuwat";
        String message_part;
        if(message.length()>25) message_part = message.substring(0, 24) + "...";
        else message_part = message;

        mList.clear();
        mList.add(new MessageItem(R.drawable.default_profile, "Claire Magz", message_part, message));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.cardList);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        RVadapter adapter = new RVadapter(getActivity().getApplicationContext(),null, mList, null, null);
        rv.setAdapter(adapter);
        return view;
    }
}
