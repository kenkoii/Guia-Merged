package ph.com.guia.Helper;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ph.com.guia.Model.Constants;
import ph.com.guia.Model.MessageItem;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.Model.Tours;
import ph.com.guia.Model.User;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class RVadapter extends RecyclerView.Adapter<RVadapter.CardViewHolder> {

    public static List<Tours> tours;
    static List<MessageItem> mi;
    static List<User> user;
    static List<PendingRequest> pr;
    static ViewGroup p;
    public static int count = 0;
    Context context;

    public RVadapter(Context context, List<Tours> tours, List<MessageItem> mi, List<User> user, List<PendingRequest> pr) {
        this.tours = tours;
        this.mi = mi;
        this.user = user;
        this.pr = pr;
        this.context = context;
    }


    @Override
    public CardViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        p=parent;
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(tours != null) {
            view = inflater.inflate(R.layout.cardview, parent, false);
        }
        else if(mi != null){
            view = inflater.inflate(R.layout.fragment_message, parent, false);
        }
        else if(user != null){
            view = inflater.inflate(R.layout.cardview_guide, parent, false);
        }
        else if(pr != null){
            view = inflater.inflate(R.layout.fragment_pending_booking, parent, false);
        }

        CardViewHolder cvh = new CardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        if(tours != null) {
            holder.location.setText(tours.get(position).tour_name);
            holder.description.setText(tours.get(position).tour_description);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeFragment.onCardClick(tours.get(position));
                }
            });

            JSONParser parser = new JSONParser(context);
            parser.getImageUrl(tours.get(position).main_image, "HomeImages", position);
        }
        else if(mi != null) {
            holder.message_name.setText(mi.get(position).name);
            holder.message_details.setText(mi.get(position).message_part);
            holder.message_image.setImageResource(mi.get(position).image);
        }
        else  if(user != null){
            holder.guide_name.setText(user.get(position).name);
            holder.guide_gendr_age.setText(user.get(position).gender.substring(0,1).toUpperCase()+
                    user.get(position).gender.substring(1)+", "+user.get(position).age);
            holder.guide_tours.setText(String.valueOf(user.get(position).tourCount));
            holder.rb.setRating(user.get(position).rating);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User u = user.get(position);
                    Toast.makeText(p.getContext(), "User: "+u.name, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(pr != null){
            holder.user_name.setText(pr.get(position).user_name);
            holder.user_gender_age.setText(pr.get(position).user_gender+" "+pr.get(position).user_age);
            holder.date_booked.setText(pr.get(position).date_booked);
            holder.tour_booked.setText(pr.get(position).tour_booked);

            final JSONParser parser = new JSONParser(context);
            parser.getImageUrl(pr.get(position).image, "AcceptBooking", position);

            holder.ivAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject request = new JSONObject();
                    try {
                        request.accumulate("_id", pr.get(position).booking_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    parser.acceptBooking(request, Constants.acceptBooking);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        int size = 0;

        if(tours != null) size = tours.size();
        else if(mi != null) size = mi.size();
        else if(user != null) size = user.size();
        else if(pr != null) size = pr.size();

        return size;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        CardView cv, message_view;
        TextView location, description, message_name, message_details, guide_name, guide_gendr_age, guide_tours;
        TextView user_name, user_gender_age, date_booked, tour_booked;
        public static ImageView iv, message_image, ivAccept, ivDecline, pending_image;
        RatingBar rb;

        CardViewHolder(View itemView) {
            super(itemView);

            cv = (CardView) itemView.findViewById(R.id.card_view);

            if(RVadapter.tours != null){
                location = (TextView) itemView.findViewById(R.id.txtLoc);
                description = (TextView) itemView.findViewById(R.id.txtDesc);
                iv = (ImageView) itemView.findViewById(R.id.imageView);
            }
            else if(RVadapter.mi != null) {
                message_view = (CardView) itemView.findViewById(R.id.card_message);
                message_image = (ImageView) itemView.findViewById(R.id.message_image);
                message_name = (TextView) itemView.findViewById(R.id.messenger);
                message_details = (TextView) itemView.findViewById(R.id.details);
            }
            else if(RVadapter.user != null){
                guide_name = (TextView) itemView.findViewById(R.id.new_travel_name);
                guide_gendr_age = (TextView) itemView.findViewById(R.id.new_travel_gender_age);
                guide_tours = (TextView) itemView.findViewById(R.id.new_travel_tour);
                rb = (RatingBar) itemView.findViewById(R.id.new_travel_rb);
            }
            else if(RVadapter.pr != null){
                user_name = (TextView) itemView.findViewById(R.id.pending_user_name);
                user_gender_age = (TextView) itemView.findViewById(R.id.pending_gender_age);
                date_booked = (TextView) itemView.findViewById(R.id.pending_date);
                tour_booked = (TextView) itemView.findViewById(R.id.pending_tour);
                ivAccept = (ImageView) itemView.findViewById(R.id.pending_accept_btn);
                ivDecline = (ImageView) itemView.findViewById(R.id.pending_decline_btn);
                pending_image = (ImageView) itemView.findViewById(R.id.pending_image);
            }
        }
    }
}
