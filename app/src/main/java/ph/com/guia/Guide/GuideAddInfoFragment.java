package ph.com.guia.Guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;
import ph.com.guia.RegisterActivity;

public class GuideAddInfoFragment extends Fragment {
    EditText txtContact, txtEmail;
    public static Spinner spnrLocation;
    Button btnNext, btnBack;
    public static String location, contact, email;
    FragmentTransaction ft;
    public static String[] location_list;
    public static ArrayAdapter<String> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_registration, container, false);

        spnrLocation = (Spinner) view.findViewById(R.id.spnrLocation);
        txtContact = (EditText) view.findViewById(R.id.txtContact);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        btnNext = (Button) view.findViewById(R.id.guide1_next);
        btnBack = (Button) view.findViewById(R.id.guide1_back);

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getAllLocations(Constants.getAllLocations, "GuideAddInfoFragment");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = spnrLocation.getSelectedItem().toString();
                contact = txtContact.getText().toString();
                email = txtEmail.getText().toString();

                if (contact.equals("")) txtContact.setError("Required!");
                else if (email.equals("")) txtEmail.setError("Required!");
                else {
                    DBHelper db = new DBHelper(getActivity().getApplicationContext());
                    db.updSetting(RegisterActivity.fb_id, 0, "isTraveler");

                    JSONObject request = new JSONObject();
                    try {
                        request.accumulate("city", "Cebu");
                        request.accumulate("country", "Philippines");
                        request.accumulate("contact_number", contact);
                        request.accumulate("email_address", email);
                        request.accumulate("type", "Arts");
                        request.accumulate("guide_user_id", RegisterActivity.user_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                    parser.postGuide(request, Constants.postGuide);
                    //JSONObject obj = parser.makeHttpRequest("http://guia.herokuapp.com/api/v1/guide", "POST", params);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegisterActivity.def == 0){
                    MainActivity.manager.logOut();
                    getActivity().finish();
                }
                else getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }
}
