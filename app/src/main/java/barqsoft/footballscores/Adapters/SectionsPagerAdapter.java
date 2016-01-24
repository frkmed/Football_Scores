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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.Activities.MainActivity;
import barqsoft.footballscores.Fragments.MainScreenFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils.Utility;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class SectionsPagerAdapter extends Fragment {
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


        TabLayout tabLayout= (TabLayout) rootView.findViewById(R.id.pager_header);


        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        tabLayout.setupWithViewPager(mPagerHandler);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        return rootView;
    }

    private class myPageAdapter extends FragmentStatePagerAdapter {
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


    }
}
