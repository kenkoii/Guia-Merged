package ph.com.guia.Traveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ph.com.guia.R;

public class FragmentNewTravel extends Fragment {
    Button btnNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_trip, container, false);

        btnNext = (Button) view.findViewById(R.id.new_travel_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBookingRequest fcg = new FragmentBookingRequest();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, fcg).addToBackStack(null).commit();
            }
        });
        return view;
    }
}
