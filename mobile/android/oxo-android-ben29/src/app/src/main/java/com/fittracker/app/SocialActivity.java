package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SocialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        Button btnShare = findViewById(R.id.btnShare);
        Button btnBack = findViewById(R.id.btnBack);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAchievement();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadSocialFeed();
    }

    private void loadSocialFeed() {
        Intent socialIntent = new Intent("com.fittracker.SOCIAL_FEED_LOADED");
        socialIntent.putExtra("user_id", "user_12345");
        socialIntent.putExtra("friends_online", 12);
        socialIntent.putExtra("recent_activities", "sarah_5km,mike_goal,emma_500cal");
        socialIntent.putExtra("user_following", "sarah,mike,emma,alex,lisa");
        socialIntent.putExtra("user_followers", "tom,jerry,bob,alice,carol");
        socialIntent.putExtra("social_score", 850);
        socialIntent.putExtra("community_rank", 23);
        sendBroadcast(socialIntent);
    }

    private void shareAchievement() {
        Intent achievementIntent = new Intent("com.fittracker.ACHIEVEMENT_SHARED");
        achievementIntent.putExtra("user_id", "user_12345");
        achievementIntent.putExtra("achievement_type", "weekly_goal_completed");
        achievementIntent.putExtra("achievement_data", "150km_this_week");
        achievementIntent.putExtra("share_platforms", "facebook,instagram,twitter");
        achievementIntent.putExtra("privacy_setting", "friends_only");
        achievementIntent.putExtra("location_shared", "Central Park");
        achievementIntent.putExtra("photo_attached", true);
        achievementIntent.putExtra("motivational_message", "Never give up on your dreams!");
        sendBroadcast(achievementIntent);

        Intent friendsIntent = new Intent("com.fittracker.FRIENDS_NOTIFIED");
        friendsIntent.putExtra("user_id", "user_12345");
        friendsIntent.putExtra("notification_type", "achievement_unlock");
        friendsIntent.putExtra("friends_list", "sarah,mike,emma,alex,lisa");
        friendsIntent.putExtra("personal_message", "Just completed my weekly goal!");
        sendBroadcast(friendsIntent);
    }
}
