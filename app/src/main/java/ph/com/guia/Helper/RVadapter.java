package ph.com.guia.Helper;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import ph.com.guia.Guide.CompleteTourFragment;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.MessageItem;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.Model.Tours;
import ph.com.guia.Model.User;
import ph.com.guia.Model.review;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.R;
import ph.com.guia.Traveler.FragmentTripBooking;
import ph.com.guia.Traveler.LoggedInTraveler;

public class RVadapter extends RecyclerView.Adapter<RVadapter.CardViewHolder> {

    public static List<Tours> tours;
    static List<MessageItem> mi;
    static List<User> user;
    static List<PendingRequest> pr;
    static List<review> rw;
    static ViewGroup p;
    static Context context;
    private ImageLoader imageLoader;
    static boolean[] ok;

    public RVadapter(Context context, List<Tours> tours, List<MessageItem> mi, List<User> user, List<PendingRequest> pr) {
        this.tours = tours;
        this.mi = mi;
        this.user = user;
        this.pr = pr;
        this.context = context;

        if(tours != null){
            ok = new boolean[tours.size()];
            CardViewHolder.due = new ArrayList<RelativeLayout>();
        }
    }

    public RVadapter(Context context, List<Tours> tours, List<MessageItem> mi, List<User> user, List<PendingRequest> pr,
                     List<review> rw) {
        this.tours = tours;
        this.mi = mi;
        this.user = user;
        this.pr = pr;
        this.rw = rw;
        this.context = context;

        if(tours != null){
            ok = new boolean[tours.size()];
            CardViewHolder.due = new ArrayList<RelativeLayout>();
        }
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
        else if(rw != null){
            view = inflater.inflate(R.layout.review_layout, parent, false);
        }

        CardViewHolder cvh = new CardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        final int index = position;

        if(tours != null) {
            imageLoader = JSONParser.getInstance(context).getImageLoader();
            imageLoader.get(tours.get(position).main_image, ImageLoader.getImageListener(holder.iv,
                    R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

            holder.iv.setImageUrl(tours.get(position).main_image, imageLoader);

            holder.location.setText(tours.get(position).tour_name);
            holder.description.setText(tours.get(position).tour_description);

            ImageView iv = new ImageView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            iv.setLayoutParams(params);
            iv.setImageResource(R.drawable.due);

            try {
                LoggedInGuide.mToolbar.setTitle("Popular Destination");

                if(tours.get(position).activity.equalsIgnoreCase("UpcomingFragment")){
                    StringTokenizer st = new StringTokenizer(tours.get(index).duration_format,"/");
                    int month = Integer.parseInt(st.nextToken());
                    int day = Integer.parseInt(st.nextToken());
                    int year = Integer.parseInt(st.nextToken());
                    boolean dueTour = false;
                    Calendar now = Calendar.getInstance();

                    Log.e("das", month+" "+day+" "+year);

                    if(year < now.get(Calendar.YEAR)) dueTour=true;
                    else if(year == now.get(Calendar.YEAR)){
                        if(month < now.get(Calendar.MONTH)+1) dueTour = true;
                        else if(month == now.get(Calendar.MONTH)+1){
                            if(day < now.get(Calendar.DAY_OF_MONTH)) dueTour = true;
                        }
                    }

                    if(dueTour){
                        if(position < holder.due.size()) {
                            holder.due.get(position).addView(iv);
                            ok[position] = true;
                        }

                    }

                    holder.cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(tours.get(index).activity.equalsIgnoreCase("UpcomingFragment")){
                                try{
                                    LoggedInGuide.mToolbar.setTitle("Tours");
                                    CompleteTourFragment ctf = new CompleteTourFragment(tours.get(index), ok[index]);
                                    FragmentTransaction ft = LoggedInGuide.fm.beginTransaction();
                                    ft.add(R.id.drawer_fragment_container, ctf).addToBackStack(null).commit();
                                }catch (Exception e){
                                    //Dialog for traveler
                                }
                            }
                        }
                    });
                }
            }catch (Exception e) {
                LoggedInTraveler.mToolbar.setTitle("Popular Destination");
                Log.e("asdadasd", tours.get(index).activity.equalsIgnoreCase("UpcomingTraveler")+" "
                    +tours.get(index).tour_id.equalsIgnoreCase("completed")+" "
                    +(position < holder.due.size())+"  "+tours.get(index).tour_id);
                if(tours.get(index).activity.equalsIgnoreCase("UpcomingTraveler")){
                    if(tours.get(index).tour_id.equalsIgnoreCase("completed")){
                        if(position < holder.due.size()) {
                            holder.due.get(position).addView(iv);
                            ok[position] = true;
                        }
                    }
                }

                holder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tours.get(index).activity.equalsIgnoreCase("HomeFragment"))
                            HomeFragment.onCardClick(tours.get(index));
                        else if(tours.get(index).activity.equalsIgnoreCase("FragmentTripBooking"))
                            FragmentTripBooking.onCardClick(tours.get(index));
                    }
                });
            }

            //MainActivity.pd.dismiss();
            HomeFragment.pd.dismiss();
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
                    User u = user.get(index);
                    Toast.makeText(p.getContext(), "User: "+u.name, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(pr != null){
            imageLoader = JSONParser.getInstance(context).getImageLoader();
            imageLoader.get(pr.get(position).image, ImageLoader.getImageListener(holder.pending_image,
                    R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

            holder.pending_image.setImageUrl(pr.get(position).image, imageLoader);
            holder.user_name.setText(pr.get(position).user_name);
            holder.user_gender_age.setText(pr.get(position).user_gender+" "+pr.get(position).user_age);
            holder.date_booked.setText(pr.get(position).date_booked);
            holder.tour_booked.setText(pr.get(position).tour_booked);

//            final JSONParser parser = new JSONParser(context);
//            parser.getImageUrl(pr.get(position).image, "AcceptBooking", position);

            holder.ivAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject request = new JSONObject();
                    try {
                        Log.e("reqid", index+"");
                        request.accumulate("_id", pr.get(index).booking_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONParser.getInstance(context).acceptBooking(request, Constants.acceptBooking);
                }
            });

            holder.ivDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject request = new JSONObject();
                    try {
                        request.accumulate("_id", pr.get(index).booking_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONParser.getInstance(context).acceptBooking(request, Constants.declineBooking);
                }
            });

            PendingFragment.pd.dismiss();
        }
        else if(rw != null){
            imageLoader = JSONParser.getInstance(context).getImageLoader();
            imageLoader.get(rw.get(position).review_image, ImageLoader.getImageListener(holder.iv,
                    R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

            holder.iv.setImageUrl(rw.get(position).review_image, imageLoader);

            holder.user_name.setText(rw.get(position).review_name);
            holder.message_details.setText(rw.get(position).review_text);
            holder.rb.setRating((float) rw.get(position).review_rate);
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
        else if(rw != null) size = rw.size();

        return size;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        CardView cv, message_view;
        TextView location, description, message_name, message_details, guide_name, guide_gendr_age, guide_tours;
        TextView user_name, user_gender_age, date_booked, tour_booked;
        public static ImageView message_image, ivAccept, ivDecline;
        public static ArrayList<RelativeLayout> due;
        NetworkImageView pending_image, iv;
        RatingBar rb;

        CardViewHolder(View itemView) {
            super(itemView);

            cv = (CardView) itemView.findViewById(R.id.card_view);

            if(RVadapter.tours != null){

                if(due.size() > tours.size()) due.clear();

                iv = (NetworkImageView) itemView.findViewById(R.id.imageView);
                due.add((RelativeLayout) itemView.findViewById(R.id.due));
                location = (TextView) itemView.findViewById(R.id.txtLoc);
                description = (TextView) itemView.findViewById(R.id.txtDesc);
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
                pending_image = (NetworkImageView) itemView.findViewById(R.id.pending_image);

            }
            else {
                if (RVadapter.rw != null) {
                    iv = (NetworkImageView) itemView.findViewById(R.id.review_image);
                    user_name = (TextView) itemView.findViewById(R.id.review_name);
                    message_details = (TextView) itemView.findViewById(R.id.review_comment);
                    rb = (RatingBar) itemView.findViewById(R.id.review_bar);
                }
            }
        }
    }
}
