package com.codepath.apps.tweeter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.tweeter.models.Tweet;
import com.codepath.apps.tweeter.models.User;

import org.parceler.Parcels;

/**
 * Created by rubab.uddin on 11/1/2016.
 */

public class UserTimelineTweetsFragment extends TweetsFragment {

    private User user;

    public UserTimelineTweetsFragment(){
    }

    public static UserTimelineTweetsFragment newInstance(FragmentManager fragmentManager, User user) {
        UserTimelineTweetsFragment frag = new UserTimelineTweetsFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        frag.setArguments(args);
        frag.fragmentManager = fragmentManager;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Parcels.unwrap(getArguments().getParcelable("user"));
    }

    @Override
    protected void populateTimeline() {
        client.getUserTimeline(maxId, user.uid, responseHandler);
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
