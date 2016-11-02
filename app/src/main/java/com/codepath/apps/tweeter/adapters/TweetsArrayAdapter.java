package com.codepath.apps.tweeter.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeter.R;
import com.codepath.apps.tweeter.activities.ProfileActivity;
import com.codepath.apps.tweeter.databinding.ItemTweetBinding;
import com.codepath.apps.tweeter.databinding.ItemTweetMediaBinding;
import com.codepath.apps.tweeter.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.tweeter.models.EmbeddedMedia;
import com.codepath.apps.tweeter.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.codepath.apps.tweeter.R.id.btnReply;


/**
 * Created by rubab.uddin on 10/27/2016.
 */
//Takes tweet object, turns it into a view to be displayed in the list
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder>{

    public static final int TWEET = 0;
    public static final int TWEET_MEDIA = 1;

    private List<Tweet> tweets;
    private Context context;
    private FragmentManager fragmentManager;
    private Tweet tweet;


    public TweetsArrayAdapter(Context context, List<Tweet> tweets, FragmentManager fragmentManager){
        this.tweets = tweets;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    private Context getContext() {
        return this.context;
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;

        if(viewType == TWEET){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolderTweet(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet_media, parent, false);
            viewHolder = new ViewHolderTweetMedia(view);
        }
        this.context = viewHolder.context;
        this.fragmentManager = viewHolder.fragmentManager;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder viewHolder, int position) {
        Tweet tweet = tweets.get(position);
        int type = getItemViewType(position);

        if(type == TWEET){
            ViewHolderTweet viewHolderTweet = (ViewHolderTweet) viewHolder;
            viewHolderTweet.onBind(tweet);
        } else {
            ViewHolderTweetMedia viewHolderTweetMedia = (ViewHolderTweetMedia) viewHolder;
            viewHolderTweetMedia.onBind(tweet);

            EmbeddedMedia embeddedMedia = tweet.embeddedMedia;
            Glide.with(context)
                    .load(embeddedMedia.mediaUrl)
                    .centerCrop().
                    into(viewHolderTweetMedia.ivMedia);
        }
        if (tweet.favorited) {
            viewHolder.btnFavorite.setBackgroundResource(R.drawable.twitter_favorite_on);
        } else {
            viewHolder.btnFavorite.setBackgroundResource(R.drawable.twitter_favorite);
        }

        if (tweet.retweeted) {
            viewHolder.btnRetweet.setBackgroundResource(R.drawable.twitter_retweet_on);
        } else {
            viewHolder.btnRetweet.setBackgroundResource(R.drawable.twitter_retweet);
        }

/*
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/HelveticaNeue-Regular.ttf");

        //find subviews to fill with data in the template
        viewHolder.tvUserName.setText(tweet.user.userName);

        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvBody.setTypeface(tf);
        viewHolder.tvBody.setMovementMethod(LinkMovementMethod.getInstance());

        viewHolder.tvProfileName.setText(tweet.user.profileName);
        viewHolder.tvProfileName.setTypeface(tf);

        viewHolder.tvTime.setText(tweet.createdAt);
        viewHolder.tvTime.setTypeface(tf);

        viewHolder.tvRetweet.setText(String.valueOf(tweet.retweetCount));
        viewHolder.tvRetweet.setTypeface(tf);

        viewHolder.tvFavorite.setText(String.valueOf(tweet.favoriteCount));
        viewHolder.tvFavorite.setTypeface(tf);

        Glide.with(getContext())
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.twitter_user)
                .error(R.drawable.twitter_user)
                .fitCenter()
                .into(viewHolder.ivProfile);
*/}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected FragmentManager fragmentManager;
        protected Context context;
        protected Tweet tweet;

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
        @BindView(R.id.btnReply)
        Button btnReply;
        @BindView(R.id.btnFavorite)
        Button btnFavorite;
        @BindView(R.id.btnRetweet)
        Button btnRetweet;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class ViewHolderTweet extends ViewHolder {
        private ItemTweetBinding binding;

        public ViewHolderTweet(View view){
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void onBind(Tweet tweet){
            this.tweet = tweet;
            binding.setTweet(tweet);
            binding.executePendingBindings();
        }
    }

    public static class ViewHolderTweetMedia extends ViewHolder {
        private ItemTweetMediaBinding binding;
        @BindView(R.id.ivMedia) ImageView ivMedia;

        public ViewHolderTweetMedia(View view){
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void onBind(Tweet tweet){
            this.tweet = tweet;
            binding.setTweet(tweet);
            binding.executePendingBindings();
        }
    }

    @OnClick(btnReply)
    public void onReply(View view) {
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(tweet);
        composeTweetDialogFragment.show(fragmentManager, "fragment_compose_tweet");
    }

    @OnClick(R.id.ivProfile)
    public void onProfilePicClick(View view) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("user", Parcels.wrap(tweet.user));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (tweets != null) {
            return tweets.size();
        }
        return 0;
    }

    public int getItemViewType(int position) {
        Tweet tweet = tweets.get(position);
        if (tweet.embeddedMedia == null) {
            return TWEET;
        } else {
            return TWEET_MEDIA;
        }
    }
}
