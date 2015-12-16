package ph.com.guia.Guide;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Navigation.FilterFragment;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.MessageFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.Navigation.SettingFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.R;

public class LoggedInGuide extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean doubleBackToExitPressedOnce = false;
    static boolean addedFrag = false;
    public static Toolbar mToolbar;
    public static ImageView nav_image;
    TextView nav_name, nav_info;
    ActionBarDrawerToggle mToggle;

    public static String name, bday, gender, age, image, location, contact, email, guide_id, fb_id;
    FragmentTransaction ft;
    HomeFragment hf = new HomeFragment();
    TripFragment tf = new TripFragment();
    SettingFragment sf = new SettingFragment();
    MessageFragment mf = new MessageFragment();
    FilterFragment ff = new FilterFragment();
    GuideProfileFragment gpf = new GuideProfileFragment();
    GuideCalendarFragment gcf = new GuideCalendarFragment();
    CreateTourFragment aif = new CreateTourFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        try{
            Bundle b = this.getIntent().getExtras();
            fb_id = b.getString("fb_id");
            name = b.getString("name");
            bday = b.getString("bday");
            gender = b.getString("gender");
            age = b.getString("age");
            image = b.getString("image");
            location = b.getString("location");
            contact = b.getString("contact");
            email = b.getString("email");
            guide_id = b.getString("guide_id");
        }
        catch(Exception e){}

        setUpHeader();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.drawer_fragment_container, hf).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setUpHeader(){
        nav_image = (ImageView) findViewById(R.id.nav_image);
        nav_name = (TextView) findViewById(R.id.nav_name);
        nav_info = (TextView) findViewById(R.id.nav_info);

        JSONParser parser = new JSONParser(this);
        parser.getImageUrl(image, "LoggedInGuide", 0);
        nav_name.setText(name);
        nav_info.setText(email);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id){
            case R.id.filter:
                mToolbar.setTitle("Filter");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, ff).addToBackStack(null).commit();
                break;
            case R.id.calendar:
                mToolbar.setTitle("Schedules");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, gcf).addToBackStack(null).commit();
                break;
            case R.id.add_trip:
                mToolbar.setTitle("Create Tour");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, aif).addToBackStack(null).commit();
                break;
            case R.id.done:
                addedFrag = false;
                this.getSupportFragmentManager().popBackStackImmediate();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        addedFrag = false;
        doubleBackToExitPressedOnce = false;

        int id = item.getItemId();

        switch(id){
            case R.id.nav_home:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, hf).commit();
                break;
            case R.id.nav_tours:
                ft = getSupportFragmentManager().beginTransaction();
//                PendingFragment pf = new PendingFragment();
//                ft.replace(R.id.drawer_fragment_container, pf).commit();
                ft.replace(R.id.drawer_fragment_container, tf).commit();
                break;
            case R.id.nav_messages:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, mf).commit();
                break;
            case R.id.nav_settings:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, sf).commit();
                break;
            case R.id.nav_logout:
                MainActivity.manager.logOut();
                LoggedInGuide.mToolbar = null;
                LoggedInGuide.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            MainActivity.end = true;
            return;
        }
        if (!addedFrag) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        else {
            addedFrag = false;
            super.onBackPressed();
            return;
        }
    }
}
