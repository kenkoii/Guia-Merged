package ph.com.guia.Navigation;

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
import ph.com.guia.Model.Trip;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class TripListFragment extends Fragment {
    public static ListView lv;
    public static ArrayList<Trip> mList = new ArrayList<Trip>();
    public static LVadapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mList.clear();
        View view = inflater.inflate(R.layout.trip_list, container, false);
        lv = (ListView) view.findViewById(R.id.trip_list);
        JSONParser.getInstance(getContext()).getTripsById(Constants.getTripsById+ LoggedInTraveler.user_id);
        return view;
    }
}
