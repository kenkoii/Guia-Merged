package ph.com.guia.Guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Note;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class GuideCalendarFragment extends Fragment {

    DateFormat formatter = SimpleDateFormat.getDateInstance();

    public static MaterialCalendarView calendar;
    TextView sched_details, sched_title;
    ImageView add_note, edit_note, delete_note;
    EditText title, detail;
    LinearLayout note_edit_delete;
    String date;
    Note note;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        calendar = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        sched_title = (TextView) view.findViewById(R.id.sched_title);
        sched_details = (TextView) view.findViewById(R.id.sched_details);
        add_note = (ImageView) view.findViewById(R.id.add_note);
        edit_note = (ImageView) view.findViewById(R.id.note_edit);
        delete_note = (ImageView) view.findViewById(R.id.note_delete);
        note_edit_delete = (LinearLayout) view.findViewById(R.id.note_edit_delete);

        final View view2 = inflater.inflate(R.layout.note_dialog, null);
        title = (EditText) view2.findViewById(R.id.note_dialog_title);
        detail = (EditText) view2.findViewById(R.id.note_dialog_details);

        calendar.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView materialCalendarView, @Nullable CalendarDay calendarDay) {
                //sched_details.setText(formatter.format(calendarDay.getDate()));
                date = String.valueOf(formatter.format(calendarDay.getDate()));

                add_note.setVisibility(View.VISIBLE);
                sched_title.setVisibility(View.VISIBLE);
                sched_details.setVisibility(View.GONE);
                note_edit_delete.setVisibility(View.GONE);

                if (GuideProfileFragment.notes.size() > 0) {
                    note = getNoteByDate(date);
                    if (note != null) {
                        add_note.setVisibility(View.GONE);
                        note_edit_delete.setVisibility(View.VISIBLE);
                        sched_details.setVisibility(View.VISIBLE);

                        sched_title.setText(note.note_title);
                        sched_details.setText(note.note_detail);

                        edit_note.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                title.setText(note.note_title);
                                detail.setText(note.note_detail);
                                showAlertDialog(view2, "Edit Note", "edit");
                            }
                        });

                        delete_note.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setIcon(R.drawable.ic_launcher);
                                builder.setTitle("Warning!");
                                builder.setMessage("\nAre you sure you want to delete note?\n");
                                builder.setNegativeButton("No", null);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JSONParser.getInstance(getContext()).deleteNote(Constants.deleteNote+note.note_id);
                                    }
                                });
                                builder.show();
                            }
                        });
                    } else {
                        sched_title.setText("Nothing to do " + date);
                    }
                } else {
                    sched_title.setText("Nothing to do " + date);
                }
            }
        });

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(view2, "Create Note", "add");
            }
        });
        return view;
    }

    public void showAlertDialog(final View view2, String dialogTitle, final String process){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setView(view2);
        builder.setTitle(dialogTitle);
        builder.setNegativeButton("Back", null);
        builder.setPositiveButton("Done", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if EditText is empty disable closing on possitive button
                if (title.getText().toString().trim().isEmpty() || title.getText().toString().length() > 15) {
                    title.requestFocus();
                    title.setError("Required! Must be less than 15 letters");
                } else if (detail.getText().toString().trim().isEmpty() || detail.getText().toString().length() > 300) {
                    detail.requestFocus();
                    detail.setError("Required! Must be less than 300 letters");
                } else {
                    if (process.equalsIgnoreCase("add")) {
                        try {
                            JSONObject request = new JSONObject();
                            request.accumulate("title", title.getText().toString());
                            request.accumulate("note_content", detail.getText().toString());
                            request.accumulate("note_date", date);
                            request.accumulate("note_guide_id", LoggedInGuide.guide_id);

                            JSONParser.getInstance(getContext()).addNote(request, Constants.addNote);
                            alertDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (process.equalsIgnoreCase("edit")) {
                        try {
                            JSONObject request = new JSONObject();
                            request.accumulate("title", title.getText().toString());
                            request.accumulate("note_content", detail.getText().toString());
                            request.accumulate("note_date", date);
                            request.accumulate("note_guide_id", LoggedInGuide.guide_id);

                            JSONParser.getInstance(getContext()).updateNote(request, Constants.updateNote + note.note_id);
                            alertDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public Note getNoteByDate(String date){
        Note note = null;
        for(int i = 0; i < GuideProfileFragment.notes.size(); i++){
            if(GuideProfileFragment.notes.get(i).note_date.equals(date)){
                note = GuideProfileFragment.notes.get(i);
                break;
            }
        }
        return note;
    }

    public static void deleteNote(String id){
        for(int i = 0; i < GuideProfileFragment.notes.size(); i++){
            if(GuideProfileFragment.notes.get(i).note_id.equals(id)){
                GuideProfileFragment.notes.remove(i);
                break;
            }
        }
    }
}
