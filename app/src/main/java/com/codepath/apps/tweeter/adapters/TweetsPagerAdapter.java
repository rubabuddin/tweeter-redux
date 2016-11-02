package com.codepath.apps.tweeter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.tweeter.fragments.HomeTimelineTweetsFragment;

/**
 * Created by rubab.uddin on 11/1/2016.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {
    private String tabHeaders[] = new String[]{"HOME", "MENTIONS"};

    private Context context;
    private FragmentManager fragmentManager;

    public TweetsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return tabHeaders.length;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return HomeTimelineTweetsFragment.newInstance(fragmentManager);
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return HomeTimelineTweetsFragment.newInstance(fragmentManager);
                //return MentionsTimelineTweetsFragment.newInstance(fragmentManager);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}
