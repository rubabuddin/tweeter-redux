package com.codepath.apps.tweeter.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.databinding.ActivityProfileBinding;
import com.codepath.apps.tweeter.fragments.UserTimelineTweetsFragment;
import com.codepath.apps.tweeter.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivBackdrop)
    ImageView ivBackdrop;
    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;
    @BindView(R.id.tvBio)
    TextView tvBio;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvProfileName) TextView tvProfileName;

    private ActivityProfileBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if(user != null){
            binding.setUser(user);
            initUserInfo();
        }
        initTimelineFragment();
    }

    private void initUserInfo(){
        tvUserName.setText(user.userName);
        tvProfileName.setText(user.profileName);
        tvFollowers.setText("" + user.followers);
        tvFollowing.setText("" + user.following);
        tvBio.setText(user.bio);


        Glide.with(this).load(user.profileImageUrl)
                .fitCenter().centerCrop()
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivProfile);

        if(user.profileBackgroundImageUrl != null){
            Glide.with(this).load(user.profileBackgroundImageUrl)
                    .fitCenter().centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                    .into(ivBackdrop);
        }
    }

    private void initTimelineFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserTimelineTweetsFragment frag = UserTimelineTweetsFragment.newInstance(getSupportFragmentManager(), user);
        fragmentTransaction.replace(R.id.flFragment, frag);
        fragmentTransaction.commit();
    }

}
