package free.ytalerts.app.gui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import free.ytalerts.app.R;
import free.ytalerts.app.businessobjects.YouTubeAPIKey;
import free.ytalerts.app.gui.businessobjects.firebase.FirebaseHelper;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String link = getIntent().getStringExtra(FirebaseHelper.FIREBASE_NOTIFICATION_LINK);

        if(link != null){
            Intent intent = com.google.android.youtube.player.YouTubeStandalonePlayer.createVideoIntent(this, YouTubeAPIKey.get().getYouTubeAPIKey(), link);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_notification);
    }
}
