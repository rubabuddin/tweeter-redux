package com.codepath.apps.tweeter.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.adapters.TweetsPagerAdapter;
import com.codepath.apps.tweeter.models.User;
import com.codepath.apps.tweeter.network.TwitterApplication;
import com.codepath.apps.tweeter.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int[] tabIcons = {R.drawable.home, R.drawable.mentions};
    private String tabTitles[] = new String[]{"Home", "Mentions"};

    private TwitterClient client;
    private User authenticatedUser;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        client = TwitterApplication.getRestClient(); //used for all endpoints across the app

        initializeViewPager();
        setAuthenticatedUser();
    }

    private void initializeViewPager(){
        viewPager.setAdapter(new TweetsPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        if (tabLayout != null) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                tabLayout.getTabAt(i).setIcon(tabIcons[i]);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setAuthenticatedUser() {
        authenticatedUser = User.getAuthenticatedUser();
        if (authenticatedUser == null) {
            getUser();
        }
    }

    private void getUser() {
        Log.d("DEBUG", "Fetching user");
        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                authenticatedUser = new User();
                Log.e("ERROR", response.toString());
                authenticatedUser.fromJSON(response);
                if (authenticatedUser != null) {
                    User.saveUser(authenticatedUser);
                } else {
                    Log.e("ERROR", "User = NULL ");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.d("DEBUG", response.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(id) {
            case R.id.navigation_profile:
                Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
                intent.putExtra("user", Parcels.wrap(authenticatedUser));
                startActivity(intent);
                break;
            case R.id.navigation_mentions:
                //start fragment?
                break;
            case R.id.navigation_trending:
                //start fragment
                break;
            default:
                break;
        }
        return true;
    }
}

