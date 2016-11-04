package com.codepath.apps.tweeter.fragments;

import android.support.v4.app.FragmentManager;

import com.codepath.apps.tweeter.models.Tweet;

/**
 * Created by rubab.uddin on 11/1/2016.
 */

public class MentionsTimelineTweetsFragment extends TweetsFragment {

    public MentionsTimelineTweetsFragment(){
    }

    public static MentionsTimelineTweetsFragment newInstance(FragmentManager fragmentManager) {
        MentionsTimelineTweetsFragment frag = new MentionsTimelineTweetsFragment();
        frag.fragmentManager = fragmentManager;
        return frag;
    }

    @Override
    protected void populateTimeline() {
        client.getMentionsTimeline(maxId, responseHandler);
    }

    @Override
    protected void updateNewTweet(Tweet newTweet) {
    }

    @Override
    protected void getOfflineData() {
    }

    @Override
    protected void setOfflineData() {
    }

    @Override
    protected void clearOfflineData() {
    }

}
