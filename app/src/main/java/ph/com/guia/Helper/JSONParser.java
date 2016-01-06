package ph.com.guia.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ph.com.guia.Guide.GuideAddInfoFragment;
import ph.com.guia.Guide.GuideProfileFragment;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.Model.Tours;
import ph.com.guia.Model.User;
import ph.com.guia.Model.review;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.Navigation.UpcomingFragment;
import ph.com.guia.R;
import ph.com.guia.RegisterActivity;
import ph.com.guia.Traveler.FragmentBookingRequest;
import ph.com.guia.Traveler.FragmentNewTrip;
import ph.com.guia.Traveler.FragmentTripBooking;
import ph.com.guia.Traveler.LoggedInTraveler;

public class JSONParser {

    private static JSONParser parser;
    Context context;
    RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    public static int size=0;

    public JSONParser(Context context) {
        this.context = context;
        this.mRequestQueue = getRequestQueue();

        imageLoader = new ImageLoader(mRequestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
    }

    public static synchronized JSONParser getInstance(Context context) {
        if (parser == null) {
            parser = new JSONParser(context);
        }
        return parser;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public void acceptBooking(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e("Accept Booking", response.toString());
                        TripFragment tf = new TripFragment();
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
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
                        //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ACCEPTBOOKING", error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getBookingsById(JSONObject request, String url, final String activity){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        size = 0;
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);

                                String tour_id, tour_name, tour_location, tour_description,
                                        duration_format, tour_preference, tour_guideId,main_image;
                                String[] additional_images;
                                int tour_duration, tour_rate, points;

                                String booking_id = req.getString("_id");
                                String date = req.getString("start_date");
                                String image = req.getJSONObject("user").getString("profImage");
                                String user_name = req.getJSONObject("user").getString("name");
                                String user_gender = req.getJSONObject("user").getString("gender");
                                String user_age = req.getJSONObject("user").getString("age");
                                tour_id = req.getJSONObject("tour").getString("id");
                                tour_name = req.getJSONObject("tour").getString("name");
                                //tour_location = req.getJSONObject("tour").getString("tour_location");
                                tour_duration = req.getJSONObject("tour").getInt("duration");
                                tour_description = req.getJSONObject("tour").getString("details");
                                //tour_preference = req.getJSONObject("tour").getString("tour_preference");
                                tour_guideId = req.getJSONObject("tour").getString("tour_guide_id");
                                tour_rate = req.getJSONObject("tour").getInt("rate");
                                main_image = req.getJSONObject("tour").getString("main_image");
                                points = req.getJSONObject("tour").getInt("points");

                                if(activity.equalsIgnoreCase("PendingFragment")) {
                                    if (req.getString("status").equalsIgnoreCase("pending")) {
                                        size++;

                                        PendingFragment.mList.add(new PendingRequest(user_name, user_age, user_gender,
                                                tour_name, date, booking_id, image));
                                        //getTourById(Constants.getTourById + tour_id, user_id, booking_id, date, activity);
                                    }

                                    if(i == response.length()-1) {
                                        PendingFragment.adapter = new RVadapter(context, null, null, null, PendingFragment.mList);
                                        PendingFragment.rv.setAdapter(PendingFragment.adapter);
                                    }
                                }else if(activity.equalsIgnoreCase("UpcomingFragment")){
                                    if (req.getString("status").equalsIgnoreCase("accepted")) {
                                        size++;

                                        UpcomingFragment.mList.add(new Tours(tour_id, tour_name, booking_id,
                                                tour_description, date, user_name, tour_guideId, tour_rate,
                                                main_image, tour_duration, null, points, "UpcomingFragment"));
                                    }

                                    if(i == response.length()-1){
                                        UpcomingFragment.adapter = new RVadapter(context, UpcomingFragment.mList, null, null, null);
                                        UpcomingFragment.rv.setAdapter(UpcomingFragment.adapter);
                                        UpcomingFragment.pd.dismiss();
                                    }
                                }else if(activity.equalsIgnoreCase("UpcomingTraveler")){
                                    if (req.getString("status").equalsIgnoreCase("accepted") ||
                                            req.getString("status").equalsIgnoreCase("completed")) {
                                        size++;

                                        UpcomingFragment.mList.add(new Tours(req.getString("status"), tour_name, booking_id,
                                                tour_description, date, user_name, tour_guideId, tour_rate,
                                                main_image, tour_duration, null, points, "UpcomingTraveler"));
                                    }

                                    if(i == response.length()-1){
                                        UpcomingFragment.adapter = new RVadapter(context, UpcomingFragment.mList, null, null, null);
                                        UpcomingFragment.rv.setAdapter(UpcomingFragment.adapter);
                                        UpcomingFragment.pd.dismiss();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(response.length()-1 == i && size == 0 &&
                                    activity.equalsIgnoreCase("PendingFragment")) PendingFragment.pd.dismiss();
                            if(response.length()-1 == i && size == 0 &&
                                    activity.equalsIgnoreCase("UpcomingFragment")) UpcomingFragment.pd.dismiss();
                        }

                        if(response.length() == 0 && activity.equalsIgnoreCase("PendingFragment")) PendingFragment.pd.dismiss();
                        else if(response.length() == 0 && activity.equalsIgnoreCase("UpcomingFragment") ||
                                activity.equalsIgnoreCase("UpcomingTraveler")) UpcomingFragment.pd.dismiss();
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

                        //Toast.makeText(context, response.length()+"", Toast.LENGTH_LONG).show();

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
                                        tour_preference, tour_guideId, tour_rate, main_image, tour_duration, null, points,"HomeFragment"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(HomeFragment.mList.size() == size) {
                                HomeFragment.adapter = new RVadapter(context, HomeFragment.mList, null, null, null);
                                HomeFragment.rv.setAdapter(HomeFragment.adapter);
                                MainActivity.pd.dismiss();
                            }
                        }

                        if(response.length() == 0){
                            MainActivity.pd.dismiss();
                            HomeFragment.pd.dismiss();
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

    public void getAllToursByPreference(JSONObject jsonObject, String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String tour_id, tour_name, tour_location, tour_description,
                                duration_format, tour_preference, tour_guideId,main_image;
                        String[] additional_images;
                        int tour_duration, tour_rate, points;

                        String[] preference = null;
                        DBHelper db = new DBHelper(context);
                        Cursor c = db.getFilter();
                        if(c.moveToFirst()){
                            String gender = c.getString(c.getColumnIndex("gender"));
                            String interest = c.getString(c.getColumnIndex("interest"));
                            preference = new String[interest.length()];
                            for(int i = 0; i<preference.length; i++){
                                switch (interest.charAt(i)){
                                    case '1': preference[i] = "Food"; break;
                                    case '2': preference[i] = "Outdoors"; break;
                                    case '3': preference[i] = "Culture"; break;
                                    case '4': preference[i] = "Music"; break;
                                    case '5': preference[i] = "Night Life"; break;
                                }
                            }
                        }
                        size = response.length();

                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                boolean ok = false;

                                if(preference != null){
                                    for(int j = 0; j < preference.length; j++){
                                        if(obj.getString("tour_preference").contains(preference[j])){
                                            ok = true;
                                            break;
                                        }
                                    }
                                }

                                if(ok) {
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

                                    FragmentTripBooking.mList.add(new Tours(tour_id, tour_name, tour_location, tour_description, duration_format,
                                            tour_preference, tour_guideId, tour_rate, main_image, tour_duration, null, points, "FragmentTripBooking"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();

                            if(i == size-1) {
                                FragmentTripBooking.adapter = new RVadapter(context, FragmentTripBooking.mList, null, null, null);
                                FragmentTripBooking.rv.setAdapter(FragmentTripBooking.adapter);
                                FragmentTripBooking.pd.dismiss();
                            }
                        }

                        if(response.length() == 0){
                            FragmentTripBooking.pd.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETALLTOURSBYPREFERENCE", error.getMessage());
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void getTourById(String url, final String user_id, final String booking_id, final String date, final String activity){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(activity.equalsIgnoreCase("PendingFragment")) {
                                String tour_name = response.getString("name");
                                getUserById(Constants.getUserById + user_id, tour_name, booking_id, date);
                            }else if(activity.equalsIgnoreCase("UpcomingFragment")){
                                String tour_id, tour_name, tour_location, tour_description,
                                        duration_format, tour_preference, tour_guideId,main_image;
                                String[] additional_images;
                                int tour_duration, tour_rate, points;

                                tour_id = response.getString("_id");
                                tour_name = response.getString("name");
                                tour_location = response.getString("tour_location");
                                tour_duration = response.getInt("duration");
                                duration_format = response.getString("duration_format");
                                tour_description = response.getString("details");
                                tour_preference = response.getString("tour_preference");
                                tour_guideId = response.getString("tour_guide_id");
                                tour_rate = response.getInt("rate");
                                main_image = response.getString("main_image");
                                points = response.getInt("points");

                                UpcomingFragment.mList.add(new Tours(tour_id, tour_name, tour_location, tour_description, date,
                                        tour_preference, tour_guideId, tour_rate, main_image, tour_duration, null, points, "Upcoming"));

                                UpcomingFragment.adapter = null;
                                UpcomingFragment.adapter = new RVadapter(context, UpcomingFragment.mList, null, null, null);
                                UpcomingFragment.rv.setAdapter(UpcomingFragment.adapter);
                                UpcomingFragment.pd.dismiss();
                            }
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

    public void getUserById(String url, final String tour_name, final String booking_id, final String date){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String image = response.getString("profImage");
                            String user_name = response.getString("name");
                            String user_gender = response.getString("gender");
                            String user_age = response.getString("age");

                            PendingFragment.mList.add(new PendingRequest(user_name, user_age, user_gender,
                                    tour_name, date, booking_id, image));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.e("SizesP", PendingFragment.mList.size()+" "+size);
                        //if(PendingFragment.mList.size() == size) {
                            PendingFragment.adapter = new RVadapter(context, null, null, null, PendingFragment.mList);
                            PendingFragment.rv.setAdapter(PendingFragment.adapter);
                        //}
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
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity)context).finish();
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
                        else if(activity.equalsIgnoreCase("RegisterActivity")){
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
                        else if(activity.equalsIgnoreCase("GuideProfile")){
                            try {
                                Bundle bundle = new Bundle();
                                bundle.putDouble("rating", response.getDouble("rating"));
                                bundle.putString("type", response.getString("type"));
                                GuideProfileFragment gpf = new GuideProfileFragment();
                                gpf.setArguments(bundle);

                                FragmentTransaction ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, gpf).commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    public void getAllLocations(String url, final String activity){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(activity.equalsIgnoreCase("GuideAddInfoFragment")) {
                                GuideAddInfoFragment.location_list = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject obj = response.getJSONObject(i);
                                    GuideAddInfoFragment.location_list[i] = obj.getString("city") + ", " + obj.getString("country");
                                }
                            }else if(activity.equalsIgnoreCase("FragmentNewTrip")){
                                FragmentNewTrip.location_list = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject obj = response.getJSONObject(i);
                                    FragmentNewTrip.location_list[i] = obj.getString("city") + ", " + obj.getString("country");
                                }
                            }
                        }catch (JSONException e) {
                            Toast.makeText(context, "Error Getting Locations", Toast.LENGTH_SHORT).show();
                        }

                        if(activity.equalsIgnoreCase("GuideAddInfoFragment")) {
                            GuideAddInfoFragment.adapter = new ArrayAdapter<String>(context,
                                    R.layout.spinner_item, GuideAddInfoFragment.location_list);

                            GuideAddInfoFragment.spnrLocation.setAdapter(GuideAddInfoFragment.adapter);
                        }else if(activity.equalsIgnoreCase("FragmentNewTrip")){
                            FragmentNewTrip.adapter = new ArrayAdapter<String>(context,
                                    R.layout.spinner_item, FragmentNewTrip.location_list);

                            FragmentNewTrip.spnrLocation.setAdapter(FragmentNewTrip.adapter);
                        }
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
                        else if(activity.equalsIgnoreCase("LoggedInGuide")){
                            LoggedInGuide.nav_image.setImageBitmap(bitmap);
                            getImageUrl(MainActivity.cover, "LoggedInGuideCover", 0);
                        }
                        else if(activity.equalsIgnoreCase("HomeImages")){
                            //RVadapter.iv.get(position).setImageBitmap(bitmap);
                            if(position == HomeFragment.llm.findLastCompletelyVisibleItemPosition() ||
                                    position == size) HomeFragment.pd.dismiss();
                        }
                        else if(activity.equalsIgnoreCase("FragmentBookingRequest")) FragmentBookingRequest.iv.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("AcceptBooking")){
                            //RVadapter.pending_image.get(position).setImageBitmap(bitmap);
                            if(position == HomeFragment.llm.findLastCompletelyVisibleItemPosition() ||
                                    position == size) PendingFragment.pd.dismiss();
                        }
                        else if(activity.equalsIgnoreCase("GuideProfile")) GuideProfileFragment.profImage.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("LoggedInGuideCover")){
                            BitmapDrawable background = new BitmapDrawable(bitmap);
                            LoggedInGuide.nav_cover.setBackgroundDrawable(background);
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

    public void getReviewsByGuideId(String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        size = 0;
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);

                                String image = req.getJSONObject("user").getString("profImage");
                                String name = req.getJSONObject("user").getString("name");
                                int rating = req.getInt("rating");
                                String review = req.getString("review");

                                GuideProfileFragment.mList.add(new review(image, name, review, rating));

                                if(i == response.length()-1){
                                    GuideProfileFragment.adapter = new RVadapter(context, null, null, null, null,
                                            GuideProfileFragment.mList);
                                    GuideProfileFragment.rv.setAdapter(GuideProfileFragment.adapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETREVIEWBYGUIDEID", error.getMessage());
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }
}