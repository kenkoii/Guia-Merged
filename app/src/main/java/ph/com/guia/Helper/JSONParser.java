package ph.com.guia.Helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ph.com.guia.Guide.GuideAddInfoFragment;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.Model.Tours;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.R;
import ph.com.guia.RegisterActivity;
import ph.com.guia.Traveler.FragmentBookingRequest;
import ph.com.guia.Traveler.LoggedInTraveler;

public class JSONParser {

    Context context;
    public String booking_id, date, tour_name;
    RequestQueue mRequestQueue;
    int size=0;

    public JSONParser(Context context) {
        this.context = context;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();
    }

    public void acceptBooking(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Accept Booking", response.toString());
                        TripFragment tf = new TripFragment();
                        LoggedInGuide.ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, tf).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ACCEPTBOOKING", error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void requestBooking(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ACCEPTBOOKING", error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getBookingsByGuideId(JSONObject request, String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        size = 0;
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);
                                if(response.getJSONObject(i).getString("status").equals("pending")) {
                                    size++;
                                    booking_id = req.getString("_id");
                                    String tour_id = req.getString("booking_tour_id");
                                    String user_id = req.getString("booking_user_id");
                                    date = req.getString("schedule");

                                    getTourById(Constants.getTourById + tour_id, user_id);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETBOOKINGSBYID", error.getMessage());
            }
        });
       mRequestQueue.add(jsonArrayRequest);
    }

    public void addTour(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Accept Booking", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ADDTOUR", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getAllTours(String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String tour_id, tour_name, tour_location, tour_description,
                                duration_format, tour_preference, tour_guideId,main_image;
                        String[] additional_images;
                        int tour_duration, tour_rate, points;

                        size = response.length();
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                tour_id = obj.getString("_id");
                                tour_name = obj.getString("name");
                                tour_location = obj.getString("tour_location");
                                tour_duration = obj.getInt("duration");
                                duration_format = obj.getString("duration_format");
                                tour_description = obj.getString("details");
                                tour_preference = obj.getString("tour_preference");
                                tour_guideId = obj.getString("tour_guide_id");
                                tour_rate = obj.getInt("rate");
                                main_image = obj.getString("main_image");
                                points = obj.getInt("points");

                                HomeFragment.mList.add(new Tours(tour_id, tour_name, tour_location, tour_description, duration_format,
                                        tour_preference, tour_guideId, tour_rate, main_image, tour_duration, null, points));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(HomeFragment.mList.size() == size-1) {
                                HomeFragment.adapter = new RVadapter(context, HomeFragment.mList, null, null, null);
                                HomeFragment.rv.setAdapter(HomeFragment.adapter);
                                HomeFragment.pd.dismiss();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETALLTOURS", error.getMessage());
            }
        });
       mRequestQueue.add(jsonArrayRequest);
    }

    public void getTourById(String url, final String user_id){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tour_name = response.getString("name");
                            getUserById(Constants.getUserById + user_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETTOURBYID", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getUserById(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String image = response.getString("profImage");
                            String user_name = response.getString("name");
                            String user_gender = response.getString("gender");
                            String user_age = response.getString("age");

                            PendingFragment.mList.add(new PendingRequest(user_name, user_age, user_gender, tour_name, date, booking_id, image));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(PendingFragment.mList.size() == size) {
                            PendingFragment.adapter = new RVadapter(context, null, null, null, PendingFragment.mList);
                            PendingFragment.rv.setAdapter(PendingFragment.adapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETUSERBYID", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void postLogin(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String user_id = response.getString("_id");
                            String guide_id = response.getString("guide_id");

                            DBHelper db = new DBHelper(context);
                            Cursor c = db.getSettingById(MainActivity.fb_id);
                            if(!c.moveToFirst()) {
                                db.addSetting(MainActivity.fb_id);
                                Intent intent = new Intent(context, RegisterActivity.class);
                                intent.putExtra("fb_id", MainActivity.fb_id);
                                intent.putExtra("name", MainActivity.name);
                                intent.putExtra("bday", MainActivity.bday);
                                intent.putExtra("gender", MainActivity.gender);
                                intent.putExtra("age", MainActivity.age);
                                intent.putExtra("image", MainActivity.image);
                                intent.putExtra("default", 1);
                                intent.putExtra("guide_id", guide_id);
                                intent.putExtra("user_id", user_id);
                                context.startActivity(intent);

                            }
                            else{
                                //Toast.makeText(getApplicationContext(), String.valueOf(c.getInt(c.getColumnIndex("isTraveler"))), Toast.LENGTH_LONG).show();
                                if(c.getInt(c.getColumnIndex("isTraveler")) == 1){
                                    Intent intent = new Intent(context, LoggedInTraveler.class);
                                    intent.putExtra("fb_id", MainActivity.fb_id);
                                    intent.putExtra("name", MainActivity.name);
                                    intent.putExtra("bday", MainActivity.bday);
                                    intent.putExtra("gender", MainActivity.gender);
                                    intent.putExtra("age", MainActivity.age);
                                    intent.putExtra("image", MainActivity.image);
                                    intent.putExtra("user_id", user_id);
                                    context.startActivity(intent);
                                }
                                else {
                                    //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();

                                    if(guide_id.equals("")){
                                        Intent intent = new Intent(context, RegisterActivity.class);
                                        intent.putExtra("fb_id", MainActivity.fb_id);
                                        intent.putExtra("guide_id", guide_id);
                                        intent.putExtra("name", MainActivity.name);
                                        intent.putExtra("bday", MainActivity.bday);
                                        intent.putExtra("gender", MainActivity.gender);
                                        intent.putExtra("age", MainActivity.age);
                                        intent.putExtra("image", MainActivity.image);
                                        intent.putExtra("default", 0);
                                        intent.putExtra("user_id", user_id);
                                        context.startActivity(intent);
                                    }
                                    else{
                                        getGuideById(Constants.getGuideById+guide_id, guide_id, "MainActivity");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTLOGIN", error.getMessage());
                MainActivity.manager.logOut();
                Toast.makeText(context, "Login Failed, Please try again.", Toast.LENGTH_LONG).show();
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void postGuide(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(context, LoggedInGuide.class);
                        intent.putExtra("fb_id", RegisterActivity.fb_id);
                        intent.putExtra("guide_id", RegisterActivity.guide_id);
                        intent.putExtra("name", RegisterActivity.name);
                        intent.putExtra("bday", RegisterActivity.bday);
                        intent.putExtra("gender", RegisterActivity.gender);
                        intent.putExtra("age", RegisterActivity.age);
                        intent.putExtra("image", RegisterActivity.image);
                        intent.putExtra("location", GuideAddInfoFragment.location);
                        intent.putExtra("contact", GuideAddInfoFragment.contact);
                        intent.putExtra("email", GuideAddInfoFragment.email);
                        context.startActivity(intent);
                        //((Activity)context).finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTGUIDE", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getGuideById(String url, final String guide_id, final String activity){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String contact = null;
                        String email = null;
                        String location = null;
                        try {
                            contact = response.getString("contact_number");
                            email = response.getString("email_address");
                            location = response.getString("city")+", "+
                                    response.getString("country");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(activity.equalsIgnoreCase("MainActivity")) {
                            Intent intent = new Intent(context, LoggedInGuide.class);
                            intent.putExtra("fb_id", MainActivity.fb_id);
                            intent.putExtra("name", MainActivity.name);
                            intent.putExtra("bday", MainActivity.bday);
                            intent.putExtra("gender", MainActivity.gender);
                            intent.putExtra("age", MainActivity.age);
                            intent.putExtra("image", MainActivity.image);
                            intent.putExtra("location", location);
                            intent.putExtra("contact", contact);
                            intent.putExtra("email", email);
                            intent.putExtra("guide_id", guide_id);
                            context.startActivity(intent);
                            //((Activity)context).finish();
                        }
                        else{
                            Intent intent = new Intent(context, LoggedInGuide.class);
                            intent.putExtra("fb_id", RegisterActivity.fb_id);
                            intent.putExtra("name", RegisterActivity.name);
                            intent.putExtra("bday", RegisterActivity.bday);
                            intent.putExtra("gender", RegisterActivity.gender);
                            intent.putExtra("age", RegisterActivity.age);
                            intent.putExtra("image", RegisterActivity.image);
                            intent.putExtra("location", location);
                            intent.putExtra("contact", contact);
                            intent.putExtra("email", email);
                            intent.putExtra("guide_id", RegisterActivity.guide_id);
                            context.startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETGUIDEBYID", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getAllLocations(String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            GuideAddInfoFragment.location_list = new String[response.length()];
                            for(int i = 0; i<response.length(); i++){
                                JSONObject obj = response.getJSONObject(i);
                                GuideAddInfoFragment.location_list[i] = obj.getString("city") + ", " + obj.getString("country");
                            }
                        }catch (JSONException e) {
                            Toast.makeText(context, "Error Getting Locations", Toast.LENGTH_SHORT).show();
                        }

                        GuideAddInfoFragment.adapter=new ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_item,GuideAddInfoFragment.location_list);

                        GuideAddInfoFragment.spnrLocation.setAdapter(GuideAddInfoFragment.adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETALLLOCATION", error.getMessage());
            }
        });
       mRequestQueue.add(jsonArrayRequest);
    }

    public void getImageUrl(String url, final String activity, final int position){
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if(activity.equalsIgnoreCase("LoggedInTraveler")) LoggedInTraveler.nav_image.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("LoggedInGuide")) LoggedInGuide.nav_image.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("HomeImages")){
                            RVadapter.iv.get(position).setImageBitmap(bitmap);

                            if(position == HomeFragment.llm.findLastCompletelyVisibleItemPosition()) HomeFragment.pd2.dismiss();
                        }
                        else if(activity.equalsIgnoreCase("FragmentBookingRequest")) FragmentBookingRequest.iv.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("AcceptBooking")){
                            RVadapter.pending_image.get(position).setImageBitmap(bitmap);

                            if(position == HomeFragment.llm.findLastCompletelyVisibleItemPosition() ||
                                    position == size) PendingFragment.pd.dismiss();
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("GETIMAGEURL", error.getMessage());
                    }
                });
       mRequestQueue.add(imageRequest);
    }
}
