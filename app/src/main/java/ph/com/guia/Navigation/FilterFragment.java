package ph.com.guia.Navigation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ph.com.guia.Helper.DBHelper;
import ph.com.guia.R;

public class FilterFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    RadioGroup rg;
    RadioButton rb_both, rb_male, rb_female;
    CheckBox cb_food, cb_outdoor, cb_culture, cb_music, cb_night_life;
    DBHelper db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new DBHelper(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        rg = (RadioGroup) view.findViewById(R.id.rg_gender);
        rb_both = (RadioButton) view.findViewById(R.id.rb_both);
        rb_male = (RadioButton) view.findViewById(R.id.rb_male);
        rb_female= (RadioButton) view.findViewById(R.id.rb_female);
        cb_food = (CheckBox) view.findViewById(R.id.cb_food);
        cb_outdoor = (CheckBox) view.findViewById(R.id.cb_outdoor);
        cb_culture = (CheckBox) view.findViewById(R.id.cb_culture);
        cb_music = (CheckBox) view.findViewById(R.id.cb_music);
        cb_night_life = (CheckBox) view.findViewById(R.id.cb_night_life);

        Cursor c = db.getFilter();
        if(c.moveToFirst()){
            String gender = c.getString(c.getColumnIndex("gender"));

            if(gender.equals("BOTH")) rb_both.setChecked(true);
            else if(gender.equals("MALE")) rb_male.setChecked(true);
            else rb_female.setChecked(true);

            String interest = c.getString(c.getColumnIndex("interest"));

            for(int i = 0; i<interest.length(); i++){
                switch (interest.charAt(i)){
                    case '1': cb_food.setChecked(true); break;
                    case '2': cb_outdoor.setChecked(true); break;
                    case '3': cb_culture.setChecked(true); break;
                    case '4': cb_music.setChecked(true); break;
                    case '5': cb_night_life.setChecked(true); break;
                }
            }
        }


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_both: db.updFilterGender("BOTH"); break;
                    case R.id.rb_male: db.updFilterGender("MALE"); break;
                    case R.id.rb_female: db.updFilterGender("FEMALE"); break;
                }
            }
        });

        cb_food.setOnCheckedChangeListener(this);
        cb_outdoor.setOnCheckedChangeListener(this);
        cb_culture.setOnCheckedChangeListener(this);
        cb_music.setOnCheckedChangeListener(this);
        cb_night_life.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String interest="";

        if(cb_food.isChecked()) interest += cb_food.getTag().toString();
        if(cb_outdoor.isChecked()) interest += cb_outdoor.getTag().toString();
        if(cb_culture.isChecked()) interest += cb_culture.getTag().toString();
        if(cb_music.isChecked()) interest += cb_music.getTag().toString();
        if(cb_night_life.isChecked()) interest += cb_night_life.getTag().toString();

        db.updFilterInterest(interest);
    }
}
