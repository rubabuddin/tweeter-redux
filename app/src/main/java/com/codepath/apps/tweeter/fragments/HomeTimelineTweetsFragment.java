package com.codepath.apps.tweeter.fragments;

import android.support.v4.app.FragmentManager;

import com.codepath.apps.tweeter.models.Tweet;

import java.util.List;

/**
 * Created by rubab.uddin on 11/1/2016.
 */

public class HomeTimelineTweetsFragment extends TweetsFragment{

    public HomeTimelineTweetsFragment() {
    }

    public static HomeTimelineTweetsFragment newInstance(FragmentManager fragmentManager) {
        HomeTimelineTweetsFragment frag = new HomeTimelineTweetsFragment();
        frag.fragmentManager = fragmentManager;
        return frag;
    }

    @Override
    protected void populateTimeline() {
        client.getHomeTimeline(maxId, responseHandler);
    }

    @Override
    protected void updateNewTweet(Tweet newTweet) {
        if (newTweet != null) {
            //add to top of timeline
            tweets.add(0, newTweet);
            aTweets.notifyItemInserted(0);
            //scroll to top after tweet post
            rvTweets.scrollToPosition(0);
        }
    }

    @Override
    protected void getOfflineData() {
        List<Tweet> savedOfflineTweets = sqlHelper.getOfflineTweets();
        if (savedOfflineTweets != null) {
            tweets.addAll(savedOfflineTweets);
            aTweets.notifyItemRangeInserted(0, savedOfflineTweets.size());
        }
    }

    @Override
    protected void setOfflineData() {
        sqlHelper.saveOfflineTweets(tweets);
    }

    @Override
    protected void clearOfflineData() {
        sqlHelper.clearTweets();
    }
}
