package ph.com.guia.Guide;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.ImageResizer;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;

public class CreateTourFragment extends Fragment implements View.OnClickListener {
    ImageView main_image, add_image;
    EditText title, description, price, duration;
    Spinner duration_format;
    Switch negotiable;
    Button btnNext;
    LayoutInflater inflater;
    Uri[] imageUri;
    Uri main_imageUri;
    int count = 0;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

        View view = inflater.inflate(R.layout.create_tour, container, false);

        main_image = (ImageView) view.findViewById(R.id.itinerary_image);
        add_image = (ImageView) view.findViewById(R.id.itinerary_add_image);
        title = (EditText) view.findViewById(R.id.itinerary_title);
        description = (EditText) view.findViewById(R.id.itinerary_desc);
        btnNext = (Button) view.findViewById(R.id.itinerary_btnNext);

        main_image.setOnClickListener(this);
        add_image.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        return view;
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public void showAlertDialog(final View view2){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setView(view2);
        builder.setTitle("Pricing");
        builder.setNegativeButton("Back", null);
        builder.setPositiveButton("Done", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if EditText is empty disable closing on possitive button
                if (price.getText().toString().trim().isEmpty()) {
                    price.setError("Required");
                } else if (duration.getText().toString().trim().isEmpty()) {
                    duration.setError("Required");
                } else {
                    if(new ConnectionChecker(getActivity().getApplicationContext()).isConnectedToInternet()){
                        Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(getActivity().getApplicationContext()));
                        try{
                            File file = new File(getRealPathFromURI(main_imageUri));
                            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                            String image = uploadResult.get("url").toString();
                            Log.e("image", image);

                            JSONObject request = new JSONObject();
                            request.accumulate("name", title.getText().toString());
                            request.accumulate("duration", duration.getText().toString());
                            request.accumulate("duration_format", "hours");
                            request.accumulate("details", description.getText().toString());
                            request.accumulate("tour_preference", "Arts");
                            request.accumulate("tour_location", "Cebu, Philippines");
                            request.accumulate("tour_guide_id", LoggedInGuide.guide_id);
                            request.accumulate("rate", price.getText().toString());
                            request.accumulate("main_image", image);

                            JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                            parser.addTour(request, Constants.addTour);
                            //Toast.makeText(getActivity().getApplicationContext(), LoggedInGuide.guide_id, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    alertDialog.dismiss();
                }
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                main_imageUri = data.getData();
                main_image.setImageURI(main_imageUri);
                //ImageResizer ir = new ImageResizer(getActivity().getApplicationContext());
                //ir.scaleImage(main_image, 425, 150);
                break;
            case 2:
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.itinerary_btnNext:
                View view2 = inflater.inflate(R.layout.create_tour_dialog, null, false);
                price = (EditText) view2.findViewById(R.id.itinerary_price);
                negotiable = (Switch) view2.findViewById(R.id.itinerary_negotiable);
                duration = (EditText) view2.findViewById(R.id.itinerary_duration);
                duration_format = (Spinner) view2.findViewById(R.id.duration_format);

                showAlertDialog(view2);
                break;
            case R.id.itinerary_image:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                this.startActivityForResult(intent, 1);
                break;
            case R.id.itinerary_add_image:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                this.startActivityForResult(intent, 2);
        }
    }
}
