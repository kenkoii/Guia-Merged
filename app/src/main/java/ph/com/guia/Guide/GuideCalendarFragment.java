package ph.com.guia.Guide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ph.com.guia.R;

public class GuideCalendarFragment extends Fragment {

    DateFormat formatter = SimpleDateFormat.getDateInstance();

    MaterialCalendarView calendar;
    TextView sched_details;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        calendar = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        sched_details = (TextView) view.findViewById(R.id.sched_details);

        calendar.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView materialCalendarView, @Nullable CalendarDay calendarDay) {
                sched_details.setText(formatter.format(calendarDay.getDate()));
            }
        });
        return view;
    }
}
