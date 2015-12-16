package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class TripFragment extends Fragment {
    //private FragmentTabHost mTabHost;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    public static ViewPagerAdapter adapter;
    PendingFragment pf = new PendingFragment();
    UpcomingFragment uf = new UpcomingFragment();
    PreviousFragment prf = new PreviousFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_tour, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mTabHost = null;
    }

    private void setupViewPager(ViewPager viewPager) {
        if(adapter == null) {
            Log.e("naa ko ari", "asdasd");
            adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
            try {
                LoggedInGuide.mToolbar.setTitle("Scheduled Tour");
                adapter.addFragment(pf, "Pending");
            } catch (Exception e) {
                LoggedInTraveler.mToolbar.setTitle("Scheduled Tour");
            }

            adapter.addFragment(uf, "Upcoming");
            adapter.addFragment(prf, "Previous");
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setAdapter(adapter);
    }
}