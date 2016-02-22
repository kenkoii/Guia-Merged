package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.LVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Reward;
import ph.com.guia.Model.Trip;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class RedeemFragment extends Fragment{
    public static ListView lv;
    public static ArrayList<Reward> mList = new ArrayList<Reward>();
    public static LVadapter adapter;
    public static ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoggedInTraveler.mToolbar.setTitle("Rewards");
        mList.clear();
        pd = ProgressDialog.show(getContext(), "Loading", "Please wait...", true, true);
        View view = inflater.inflate(R.layout.trip_list, container, false);
        lv = (ListView) view.findViewById(R.id.trip_list);

        JSONParser.getInstance(getContext()).getRewards(Constants.getRewards);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mList.size() == 0) lv.setBackgroundResource(R.drawable.no_result);

        LoggedInTraveler.mToolbar.setTitle("Rewards");
        lv.setAdapter(adapter);
    }
}
