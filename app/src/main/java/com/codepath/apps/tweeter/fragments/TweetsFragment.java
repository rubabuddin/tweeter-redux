package com.codepath.apps.tweeter.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.activities.DetailsActivity;
import com.codepath.apps.tweeter.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweeter.helpers.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweeter.helpers.ItemClickSupport;
import com.codepath.apps.tweeter.helpers.SQLHelper;
import com.codepath.apps.tweeter.models.Tweet;
import com.codepath.apps.tweeter.network.TwitterApplication;
import com.codepath.apps.tweeter.network.TwitterClient;
import com.codepath.apps.tweeter.network.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by rubab.uddin on 11/1/2016.
 */

public abstract class TweetsFragment extends Fragment implements ComposeTweetDialogFragment.ComposeTweetDialogListener{

    protected Context mContext;
    protected SQLHelper sqlHelper;
    protected TwitterClient client;
    protected TweetsArrayAdapter aTweets;
    protected FragmentManager fragmentManager;
    protected List<Tweet> tweets;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    protected long maxId = -1;

    protected abstract void populateTimeline();
    protected abstract void getOfflineData();
    protected abstract void setOfflineData();
    protected abstract void clearOfflineData();
    protected abstract void updateNewTweet(Tweet newTweet);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = getActivity();
        client = TwitterApplication.getRestClient();
        sqlHelper = SQLHelper.getHelper();

        setupSwipeToRefresh();
        //setAuthenticatedUser();
        initializeTimeline();
    }

    private void setupSwipeToRefresh() {
        // Lookup the swipe container view
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //initializeTimeline();
                maxId = -1;
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initializeTimeline() {
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(mContext, tweets, fragmentManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setHasFixedSize(true);
        rvTweets.setAdapter(aTweets);

        swipeContainer.setRefreshing(false);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                maxId = tweets.get(tweets.size() -1).uid - 1;
                populateTimeline();
            }
        });

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        intent.putExtra("details", Parcels.wrap(tweet));
                        startActivity(intent);
                    }
                });
        if (!Utils.isOnline()) {
            //get offline tweets
            getOfflineData();
        } else {
            maxId = -1;
            populateTimeline();
        }
    }

    //generic response handler for all fragments
    protected JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("DEBUG", "getting tweets: " + response.toString());
            if (maxId == -1) {
                int tweetsListSize = tweets.size();
                tweets.clear();
                aTweets.notifyItemRangeRemoved(0, tweetsListSize);
                Log.d("DEBUG", aTweets.toString());
                //clearOfflineData();
            }

            int curSize = tweets.size();
            tweets.addAll(Tweet.fromJSONArray(response));
            aTweets.notifyItemRangeInserted(curSize, tweets.size());
            setOfflineData();
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("DEBUG", errorResponse.toString());
        }
    };

    @OnClick(R.id.fabComposeTweet)
    public void composeTweet() {
        FragmentManager fragmentManager = getChildFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(null);
        composeTweetDialogFragment.show(fragmentManager, "fragment_compose");
    }

    public void onUpdateStatusSuccess(Tweet tweet){
        updateNewTweet(tweet);
    }
}
