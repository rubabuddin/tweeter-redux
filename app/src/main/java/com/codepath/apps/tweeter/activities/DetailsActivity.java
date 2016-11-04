package com.codepath.apps.tweeter.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.models.Tweet;
import com.codepath.apps.tweeter.network.TwitterApplication;
import com.codepath.apps.tweeter.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvProfileName)
    TextView tvProfileName;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvRetweet)
    TextView tvRetweet;
    @BindView(R.id.tvFavorite)
    TextView tvFavorite;
    @BindView(R.id.etReply)
    EditText etReply;
    @BindView(R.id.btnReplyTweet)
    Button btnReplyTweet;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private TwitterClient twitterClient;
    private Tweet tweet;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        twitterClient = TwitterApplication.getRestClient();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("details"));
        if (tweet != null) {
            initDetails();
        } else {
            Log.e("ERROR", "Tweet not found");
        }
    }

    private void initDetails() {
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeue-Regular.ttf");

        //find subviews to fill with data in the template
        tvUserName.setText(tweet.user.userName);

        tvBody.setText(tweet.body);
        tvBody.setTypeface(tf);
        tvBody.setMovementMethod(LinkMovementMethod.getInstance());

        tvProfileName.setText(tweet.user.profileName);
        tvProfileName.setTypeface(tf);

        tvTime.setText(tweet.timeAgo);
        tvTime.setTypeface(tf);

        tvRetweet.setText(String.valueOf(tweet.retweetCount));
        tvRetweet.setTypeface(tf);

        tvFavorite.setText(String.valueOf(tweet.favoriteCount));
        tvFavorite.setTypeface(tf);

        etReply.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    etReply.setText(tweet.user.userName);
                    btnReplyTweet.setEnabled(true);
                }else {
                    etReply.setHint("Reply to this tweet");
                    btnReplyTweet.setEnabled(false);
                }
            }
        });

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.twitter_user)
                .error(R.drawable.twitter_user)
                .fitCenter()
                .into(ivProfile);

    }

    @OnClick(R.id.ivProfile)
    public void onProfilePicClick(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", Parcels.wrap(tweet.user));
        this.startActivity(intent);
    }

    @OnClick(R.id.btnReplyTweet)
    public void replyTweet() {
        btnReplyTweet.setEnabled(false);
        View parentLayout = findViewById(R.id.activity_details);
        final Snackbar snackBarSuccess = Snackbar.make(parentLayout, "Tweet sent to " + tweet.user.userName, Snackbar.LENGTH_INDEFINITE);;
        final Snackbar snackBarFailure = Snackbar.make(parentLayout, "Tweet failed to send, try again", Snackbar.LENGTH_INDEFINITE);;

        twitterClient.postStatus(etReply.getText().toString(), -1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                snackBarSuccess.show();
                Tweet newTweet = new Tweet();
                try{
                    newTweet.fromJSON(response);
                } catch (JSONException e){
                    e.printStackTrace();
                }
                if (newTweet != null) {
                    InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(etReply.getWindowToken(), 0);
                    etReply.setText("");
                    etReply.clearFocus();
                } else {
                    Log.d("DEBUG", "Compose tweet failed: " + response.toString());
                }
            }
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                snackBarFailure.show();
            }
        });
    }





}
