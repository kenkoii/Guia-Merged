package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class PendingFragment extends Fragment {

    public static ArrayList<PendingRequest> mList = new ArrayList<PendingRequest>();
    public static RecyclerView rv;
    public static RVadapter adapter;
    public static ProgressDialog pd;
    public static LinearLayoutManager llm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);

        mList.clear();
        if(LoggedInGuide.mToolbar != null){
            try {
                JSONObject request = new JSONObject();
                request.accumulate("booking_guide_id", LoggedInGuide.guide_id);
                JSONParser.getInstance(getContext()).getBookingsById(request, Constants.getBookingsByGuideId, "PendingFragment");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                JSONObject request = new JSONObject();
                request.accumulate("booking_user_id", LoggedInTraveler.user_id);
                Log.e("id", LoggedInTraveler.user_id);
                JSONParser.getInstance(getContext()).getBookingsById(request, Constants.getBookingsByUserId, "PendingFragment");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        rv = (RecyclerView) view.findViewById(R.id.cardList);
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mList.size() == 0) rv.setBackgroundResource(R.drawable.no_result);
    }
}
