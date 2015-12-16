package ph.com.guia.Traveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import ph.com.guia.Model.Tours;
import ph.com.guia.R;

public class FragmentTripBooking extends Fragment {

    Tours c;
    TextView name, gender_age, tours;
    RatingBar rb;
    Button btnBook;

//    public FragmentTripBooking(Tours c) {
//        this.c = c;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_booking, container, false);

//        name = (TextView) view.findViewById(R.id.course_name);
//        gender_age = (TextView) view.findViewById(R.id.course_gender_age);
//        tours = (TextView) view.findViewById(R.id.course_tour);
//        rb = (RatingBar) view.findViewById(R.id.course_rb);
//        btnBook = (Button) view.findViewById(R.id.course_book);

//        name.setText(c.user.name);
//        gender_age.setText(c.user.gender.substring(0,1).toUpperCase()+c.user.gender.substring(1)+", "+
//                c.user.age);
//        tours.setText(c.user.tourCount);
//        rb.setRating(c.user.rating);

        return view;
    }
}
