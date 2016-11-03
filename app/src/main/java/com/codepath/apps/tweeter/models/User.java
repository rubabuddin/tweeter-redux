package com.codepath.apps.tweeter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by rubab.uddin on 10/25/2016.
 */

@Parcel (analyze = {User.class})
@Table(name = "User")
public class User extends Model {

    @Column(name = "UserId")
    public long uid;

    @Column(name = "ProfileName") //Kevin Durant
    public String profileName;

    @Column(name = "UserName") //@kdtrey5
    public String userName;

    @Column(name = "ProfileImageUrl")
    public String profileImageUrl;

    @Column(name = "ProfileBackgroundImageUrl")
    public String profileBackgroundImageUrl;

    @Column(name = "Followers")
    public int followers;

    @Column(name = "Following")
    public int following;

    @Column(name = "Bio")
    public String bio;

    // empty constructor needed by ActiveAndroid and the Parceler library
    public User() {
        super();
    }

    //deserialize the user JSON => USer
    public void fromJSON(JSONObject json) {
        //Extract and fill the values
        try {
            userName = "@" + json.getString("screen_name");
            profileName = json.getString("name");
            uid = json.getLong("id");
            profileImageUrl = json.getString("profile_image_url");
            profileBackgroundImageUrl = json.getString("profile_background_image_url");
            followers = json.getInt("followers_count");
            following = json.getInt("friends_count");
            bio = json.getString("bio");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveUser(User user) {
        user.save();
    }

    public static User getAuthenticatedUser() {
        List<User> users = new Select().from(User.class).execute();
        if (users != null && users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }
}

