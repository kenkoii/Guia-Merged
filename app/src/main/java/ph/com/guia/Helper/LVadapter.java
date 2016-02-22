package ph.com.guia.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Reward;
import ph.com.guia.Model.Trip;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class LVadapter extends ArrayAdapter<Trip> {
    ArrayList<Trip> trips;
    ArrayList<Reward> reward;
    Context context;
    boolean ok = false;

    public LVadapter(Context context, ArrayList<Trip> trips) {
        super(context, 0, trips);
        this.context = context;
        this.trips = trips;
    }

    public LVadapter(Context context, ArrayList<Trip> trips, ArrayList<Reward> reward) {
        super(context, 0, trips);
        this.context = context;
        this.trips = trips;
        this.reward = reward;
    }

    @Override
    public int getCount() {
        int size = 0;

        if(trips != null) size = trips.size();
        else if(reward != null) size = reward.size();

        return size;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index = position;
        if (trips != null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trip_list_detail, parent, false);

           NetworkImageView iv = (NetworkImageView) convertView.findViewById(R.id.trip_image);

            ImageLoader imageLoader = JSONParser.getInstance(getContext()).getImageLoader();
            imageLoader.get(trips.get(position).image, ImageLoader.getImageListener(iv,
                    R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

            iv.setImageUrl(trips.get(position).image, imageLoader);

            TextView location = (TextView) convertView.findViewById(R.id.trip_location);
            TextView detail = (TextView) convertView.findViewById(R.id.trip_detail);

            location.setText("To: "+trips.get(position).location);
            detail.setText(trips.get(position).description);
        }else if(reward != null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reward, parent, false);

            NetworkImageView iv = (NetworkImageView) convertView.findViewById(R.id.reddem_imageView);

            ImageLoader imageLoader = JSONParser.getInstance(getContext()).getImageLoader();
            imageLoader.get(reward.get(position).image, ImageLoader.getImageListener(iv,
                    R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

            iv.setImageUrl(reward.get(position).image, imageLoader);

            TextView name = (TextView) convertView.findViewById(R.id.redeem_txtLoc);
            TextView detail = (TextView) convertView.findViewById(R.id.redeem_txtDesc);
            TextView location = (TextView) convertView.findViewById(R.id.redeem_location);
            TextView points = (TextView) convertView.findViewById(R.id.redeem_points);
            final Button redeem = (Button) convertView.findViewById(R.id.redeem_button);

            if(MainActivity.points < Double.parseDouble(reward.get(position).points)){
                redeem.setBackgroundResource(R.drawable.rounded_button_disable);
            }else{
                redeem.setBackgroundResource(R.drawable.rounded_button);
                redeem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setIcon(R.drawable.guia_dialog);
                        builder.setTitle("Redeem");
                        builder.setMessage("Tour Name: " + reward.get(index).tour_name +
                                "\nTour Location: " + reward.get(index).tour_location +
                                "\nPoints to Deduct: " + reward.get(index).points +
                                "\nCurrent Points: " + MainActivity.points);
                        builder.setNegativeButton("No", null);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    JSONObject request = new JSONObject();
                                    request.accumulate("_id", reward.get(index).id);
                                    request.accumulate("user_id", LoggedInTraveler.user_id);
                                    request.accumulate("points", reward.get(index).points);
                                    JSONParser.getInstance(context).redeemReward(request, Constants.redeemReward);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            name.setText(reward.get(position).tour_name);
            detail.setText(reward.get(position).tour_details);
            location.setText("Location: "+ reward.get(position).tour_location);
            points.setText("Points Required: "+reward.get(position).points);
        }
        return convertView;
    }
}
