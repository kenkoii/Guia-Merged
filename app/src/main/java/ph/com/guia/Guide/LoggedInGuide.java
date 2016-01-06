package ph.com.guia.Guide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Navigation.FilterFragment;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.MessageFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.Navigation.PreviousFragment;
import ph.com.guia.Navigation.SettingFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.Navigation.UpcomingFragment;
import ph.com.guia.R;

public class LoggedInGuide extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean doubleBackToExitPressedOnce = false;
    static boolean addedFrag = false;
    public static Toolbar mToolbar;
    public static ImageView nav_image;
    public static LinearLayout nav_cover;
    public static FragmentManager fm;

    TextView nav_name, nav_info;
    DrawerLayout drawer;
    ActionBarDrawerToggle mToggle;


    public static String name, bday, gender, age, image, location, contact, email, guide_id, fb_id;
    public static FragmentTransaction ft;
    HomeFragment hf = new HomeFragment();
    SettingFragment sf = new SettingFragment();
    MessageFragment mf = new MessageFragment();
    FilterFragment ff = new FilterFragment();
    //GuideProfileFragment gpf = new GuideProfileFragment();
    GuideCalendarFragment gcf = new GuideCalendarFragment();
    CreateTourFragment aif = new CreateTourFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        fm = getSupportFragmentManager();
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setUpHeader();

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
        nav_cover = (LinearLayout) findViewById(R.id.nav_cover);
        nav_name = (TextView) findViewById(R.id.nav_name);
        nav_info = (TextView) findViewById(R.id.nav_info);

        JSONParser parser = new JSONParser(this);
        parser.getImageUrl(image, "LoggedInGuide", 0);

        nav_name.setText(name);
        nav_info.setText(email);

        nav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, gpf).commit();
                JSONParser.getInstance(LoggedInGuide.this).getGuideById(Constants.getGuideById+guide_id, guide_id, "GuideProfile");
                drawer.closeDrawer(GravityCompat.START);
            }
        });
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
                TripFragment tf = new TripFragment();
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
            case R.id.nav_share:
//                MainActivity.manager.getInstance().logInWithPublishPermissions(
//                        this,
//                        Arrays.asList("publish_actions"));
                try{
                    //Uri img = data.getData();
                    ShareDialog shareDialog = new ShareDialog(this);

                    if (ShareDialog.canShow(SharePhotoContent.class)) {
                        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.guia_logo);
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(bitmap)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();

                        shareDialog.show(content);
                    }

                }catch (Exception e){e.printStackTrace();}
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                this.startActivityForResult(intent, 1);
                break;
//            case R.id.nav_pending:
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, pdf).commit();
//                break;
//            case R.id.nav_upcoming:
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, upf).commit();
//                break;
//            case R.id.nav_previous:
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, pvf).commit();
//                break;
            case R.id.nav_logout:
                MainActivity.manager.logOut();
                LoggedInGuide.mToolbar = null;
                LoggedInGuide.this.finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            Uri img = data.getData();
            ShareDialog shareDialog = new ShareDialog(this);

            if (ShareDialog.canShow(SharePhotoContent.class)) {
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.guia_logo);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);
            }

        }catch (Exception e){e.printStackTrace();}
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
