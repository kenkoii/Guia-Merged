package ph.com.guia.Traveler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ph.com.guia.Helper.ImageLoadTask;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;

public class FragmentBookingRequest extends Fragment{

    public static ImageView iv;
    private static final String CONFIG_CLIENT_ID = "AdSWgpp_bt-NLQMoZBDquci7RxnqGAHAyww92qH2NBgrzYR6uVZj3AOdEEeg50B4IybD0y0wICbirfj6";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static PayPalConfiguration config;

    Tours tour;
    TextView title, description, rate, points, duration, guide;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(CONFIG_CLIENT_ID)
                .acceptCreditCards(false);
        tour = getArguments().getParcelable("tour");

        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getContext().startService(intent);

        View view = inflater.inflate(R.layout.fragment_trip_detials, container, false);
        iv = (ImageView) view.findViewById(R.id.detail_main_image);
        title = (TextView) view.findViewById(R.id.detail_title);
        description = (TextView) view.findViewById(R.id.detail_description);
        rate = (TextView) view.findViewById(R.id.detail_rate);
        points = (TextView) view.findViewById(R.id.detail_points);
        duration = (TextView) view.findViewById(R.id.detail_duration);
        guide = (TextView) view.findViewById(R.id.detail_guide);

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getImageUrl(tour.main_image, "FragmentBookingRequest", 0);

        title.setText(tour.tour_name);
        description.setText(tour.tour_description);
        rate.setText("Rate: "+tour.tour_rate);
        points.setText("Points Reward: "+tour.points);
        duration.setText("Duration: "+tour.tour_duration+" "+tour.duration_format);

        Button btnBook = (Button) view.findViewById(R.id.detail_book);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final double rate;
                String charge = "50PHP";
                if(tour.tour_rate <= 500) rate = 50;
                else{
                    rate = tour.tour_rate * 0.1;
                    charge = "10%";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("Notice");
                builder.setMessage("\nTour price: "+tour.tour_rate+"\nService Charge: "+charge+
                        "\nEstimated Tour Expense: "+(tour.tour_rate+rate)+
                        "\n\nCharge upon booking: "+rate+"\n");
                builder.setNegativeButton("Back", null);
                builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PayPalPayment ppp = new PayPalPayment(new BigDecimal(String.valueOf(rate)), "PHP",
                                tour.tour_name, PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent = new Intent(getContext(),
                                PaymentActivity.class);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, ppp);
                        startActivityForResult(intent, 1);
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));

                        JSONObject request = new JSONObject();
                        try {
                            request.accumulate("booking_guide_id", tour.tour_guideId);
                            request.accumulate("booking_tour_id", tour.tour_id);
                            request.accumulate("booking_user_id", LoggedInTraveler.user_id);
                            request.accumulate("start_date", "12/28/2015");
                            request.accumulate("end_date", "12/28/2015");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                        parser.requestBooking(request, Constants.requestBooking);
                        //JSONObject obj = parser.makeHttpRequest("http://guia.herokuapp.com/api/v1/book", "POST", params);
                        //Toast.makeText(getActivity().getApplicationContext(), "CLicked!"+LoggedInTraveler.user_id, Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        Toast.makeText(getContext(), "Successfully Booked!",
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out.println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
}
