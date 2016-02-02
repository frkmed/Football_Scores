package barqsoft.footballscores.Adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.Activities.MainActivity;
import barqsoft.footballscores.Fragments.MainScreenFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils.PageChangeListener;
import barqsoft.footballscores.Utils.Utility;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class SectionsPagerAdapter extends Fragment {


    final String LOG_TAG = SectionsPagerAdapter.class.getSimpleName();

    public static final int NUM_PAGES = 5;
    public static ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;



    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.colapsing_toolbar);

        // Make the title invisible while the toolbar is expanded.
        int transparent_color = getActivity().getResources().getColor(R.color.transparent);
        collapsingToolbar.setExpandedTitleColor(transparent_color);
        final TextView toolbarText = (TextView) rootView.findViewById(R.id.toolbar_match_count);
        final TextView outgoingText = (TextView) rootView.findViewById(R.id.toolbar_match_count_outgoing);


        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.pager_header);


        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        for (int i = 0; i < NUM_PAGES; i++)
        {
            Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.addOnPageChangeListener(PageChangeListener.newInstance(mPagerAdapter, toolbarText, outgoingText));
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        tabLayout.setupWithViewPager(mPagerHandler);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        return rootView;
    }

    public class myPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i) {
            return viewFragments[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            String dayName = Utility.getDayName(getActivity(), System.currentTimeMillis() + ((position - 2) * 86400000));
            return dayName;
        }

        public String getDayMatchCount(int position)
        {
            Date fragmentdate = new Date(System.currentTimeMillis() + ((position - 2) * 86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            String dayMatchCount = Utility.getMatchDayCount(mformat.format(fragmentdate),getActivity());
            return dayMatchCount;
        }

    }

}
