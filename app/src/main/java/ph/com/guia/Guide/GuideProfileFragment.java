package ph.com.guia.Guide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import ph.com.guia.Helper.ImageLoadTask;
import ph.com.guia.R;

public class GuideProfileFragment extends Fragment {
    String specialty = "Trecking";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LoggedInGuide.mToolbar.setTitle("Profile");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.guide_calendar, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_profile, container, false);

        ImageView profImage = (ImageView) view.findViewById(R.id.main_profile_image);
        TextView profName = (TextView) view.findViewById(R.id.main_profile_name);
        TextView profEmail = (TextView) view.findViewById(R.id.main_profile_email);
        TextView profAge = (TextView) view.findViewById(R.id.main_profile_age);
        TextView profLocation = (TextView) view.findViewById(R.id.location_info);
        TextView profSpecialty = (TextView) view.findViewById(R.id.specialty_info);
        TextView profNumber = (TextView) view.findViewById(R.id.number_info);
        RatingBar rb = (RatingBar) view.findViewById(R.id.rating);


        new ImageLoadTask(LoggedInGuide.image, profImage).execute();
        profName.setText(LoggedInGuide.name);
        profEmail.setText(LoggedInGuide.email);
        profAge.setText(LoggedInGuide.age+" years old");
        profLocation.setText(LoggedInGuide.location);
        profSpecialty.setText(specialty);
        profNumber.setText(LoggedInGuide.contact);
        rb.setMax(5);
        rb.setNumStars(5);
        rb.setRating(4);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggedInGuide.mToolbar.setTitle("Profile");
        LoggedInGuide.doubleBackToExitPressedOnce = false;
    }
}
