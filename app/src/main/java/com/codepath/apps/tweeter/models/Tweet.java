package com.codepath.apps.tweeter.models;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by rubab.uddin on 10/25/2016.
 */
//parse JSON, store data, encapsulate state logic or display logic
@Parcel(analyze = {Tweet.class})
@Table(name = "Tweets")
public class Tweet extends Model{

    @Column(name = "TweetId")
    public long uid; //unique id for the tweet

    @Column(name = "Body")
    public String body; //tweet content

    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user; // store embedded User object

    @Column(name = "CreatedAt")
    public String createdAt;

    @Column(name = "TimeAgo")
    public String timeAgo;

    @Column(name = "Retweeted")
    public boolean retweeted;

    @Column(name = "RetweetCount")
    public long retweetCount;

    @Column(name = "Favorited")
    public boolean favorited;

    @Column(name = "FavoriteCount")
    public long favoriteCount;

    @Column(name = "EmbeddedMedia", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public EmbeddedMedia embeddedMedia;

    // empty constructor needed by ActiveAndroid and the Parceler library
    public Tweet() {
        super();
    }

    public void getTimeAgo() {
        String tempTimeAgo = getRelativeTimeAgo(createdAt);
        String regex = "^(\\d+)\\s(\\S).*";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(tempTimeAgo);

        while (matcher.find()) {
            timeAgo = matcher.group(1) + matcher.group(2);
        }
    }

    //Deserialize the JSON and build tweet object
    //Tweet.fromJSON("..") -> <Tweet>
    public void fromJSON(JSONObject jsonObject) throws JSONException {
        //Tweet model
        uid = jsonObject.getLong("id");
        body = jsonObject.getString("text");
        createdAt = jsonObject.getString("created_at");
        retweeted = jsonObject.getBoolean("retweeted");
        retweetCount = jsonObject.getLong("retweet_count");
        favorited = jsonObject.getBoolean("favorited");
        favoriteCount = jsonObject.getLong("favorite_count");
        getTimeAgo(); //reformats createdAt time string

        //User model
        user = new User();
        user.fromJSON(jsonObject.getJSONObject("user"));


        //EmbeddedMedia model
        embeddedMedia = new EmbeddedMedia();

        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities != null) {
            try {
                JSONArray embeddedMediaArray = entities.getJSONArray("media");
                if (embeddedMediaArray != null) {
                    List<EmbeddedMedia> embeddedMediaList = new ArrayList<EmbeddedMedia>();
                    for (int i = 0; i < embeddedMediaArray.length(); i++) {
                        try {
                            JSONObject embeddedMediaJson = embeddedMediaArray.getJSONObject(i);
                            EmbeddedMedia foundMedia = new EmbeddedMedia();
                            foundMedia.fromJSON(embeddedMediaJson);
                            if (foundMedia != null)
                                embeddedMediaList.add(foundMedia);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                    if (embeddedMediaList != null) {
                        embeddedMedia = embeddedMediaList.get(0); //get first item
                    }
                }
            } catch (JSONException e) {
                Log.d("DEBUG", "no media found");
                embeddedMedia = null;
            }
        }
    }

    //Tweet.fromJSONArray => List<Tweet>
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++){
            try{
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = new Tweet();
                tweet.fromJSON(tweetJson);
                if(tweet != null)
                    tweets.add(tweet);
            } catch (JSONException e){
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

}

/*
		[
		  {
		    "coordinates": null,
		    "truncated": false,
		    "created_at": "Tue Aug 28 21:16:23 +0000 2012",
		    "favorited": false,
		    "id_str": "240558470661799936",
		    "in_reply_to_user_id_str": null,
		    "entities": {
		      "urls": [
		 
		      ],
		      "hashtags": [
		 
		      ],
		      "user_mentions": [
		 
		      ]
		    },
		    "text": "just another test",
		    "contributors": null,
		    "id": 240558470661799936,
		    "retweet_count": 0,
		    "in_reply_to_status_id_str": null,
		    "geo": null,
		    "retweeted": false,
		    "in_reply_to_user_id": null,
		    "place": null,
		    "source": "<a href="//realitytechnicians.com%5C%22" rel="\"nofollow\"">OAuth Dancer Reborn</a>",
		    "user": {
		      "name": "OAuth Dancer",
		      "profile_sidebar_fill_color": "DDEEF6",
		      "profile_background_tile": true,
		      "profile_sidebar_border_color": "C0DEED",
		      "profile_image_url":"http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
		      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
		      "location": "San Francisco, CA",
		      "follow_request_sent": false,
		      "id_str": "119476949",
		      "is_translator": false,
		      "profile_link_color": "0084B4",
		      "entities": {
		        "url": {
		          "urls": [
		            {
		              "expanded_url": null,
		              "url": "http://bit.ly/oauth-dancer",
		              "indices": [
		                0,
		                26
		              ],
		              "display_url": null
		            }
		          ]
		        },
		        "description": null
		      },
		      "default_profile": false,
		      "url": "http://bit.ly/oauth-dancer",
		      "contributors_enabled": false,
		      "favourites_count": 7,
		      "utc_offset": null,
		      "profile_image_url_https":"https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
		      "id": 119476949,
		      "listed_count": 1,
		      "profile_use_background_image": true,
		      "profile_text_color": "333333",
		      "followers_count": 28,
		      "lang": "en",
		      "protected": false,
		      "geo_enabled": true,
		      "notifications": false,
		      "description": "",
		      "profile_background_color": "C0DEED",
		      "verified": false,
		      "time_zone": null,
		      "profile_background_image_url_https":"https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
		      "statuses_count": 166,
		      "profile_background_image_url":"http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
		      "default_profile_image": false,
		      "friends_count": 14,
		      "following": false,
		      "show_all_inline_media": false,
		      "screen_name": "oauth_dancer"
		    },
		    "in_reply_to_screen_name": null,
		    "in_reply_to_status_id": null
		  },

 */