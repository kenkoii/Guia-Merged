package ph.com.guia.Traveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.guia.Helper.ImageLoadTask;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;

public class FragmentBookingRequest extends Fragment{

    Tours tour;
    public static ImageView iv;
    TextView title, description, rate, points, duration, guide;

    public FragmentBookingRequest() {}

    public FragmentBookingRequest(Tours tour) {
        this.tour = tour;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_detials, container, false);
        iv = (ImageView) view.findViewById(R.id.detail_main_image);
        title = (TextView) view.findViewById(R.id.detail_title);
        description = (TextView) view.findViewById(R.id.detail_description);
        rate = (TextView) view.findViewById(R.id.detail_rate);
        points = (TextView) view.findViewById(R.id.detail_points);
        duration = (TextView) view.findViewById(R.id.detail_duration);
        guide = (TextView) view.findViewById(R.id.detail_guide);

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getImageUrl(tour.main_image, "FragmentBookingRequest", 0);

        title.setText(tour.tour_name);
        description.setText(tour.tour_description);
        rate.setText("Rate: "+tour.tour_rate);
        points.setText("Points Reward: "+tour.points);
        duration.setText("Duration: "+tour.tour_duration+" "+tour.duration_format);

        Button btnBook = (Button) view.findViewById(R.id.detail_book);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject request = new JSONObject();
                try {
                    request.accumulate("booking_guide_id", tour.tour_guideId);
                    request.accumulate("booking_tour_id", tour.tour_id);
                    request.accumulate("booking_user_id", LoggedInTraveler.user_id);
                    request.accumulate("start_date", "12/28/2015");
                    request.accumulate("end_date", "12/28/2015");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                parser.requestBooking(request, Constants.requestBooking);
                //JSONObject obj = parser.makeHttpRequest("http://guia.herokuapp.com/api/v1/book", "POST", params);
                //Toast.makeText(getActivity().getApplicationContext(), "CLicked!"+LoggedInTraveler.user_id, Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }
}
