package free.ytalerts.app.gui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import free.ytalerts.app.R;
import free.ytalerts.app.gui.businessobjects.firebase.FirebaseHelper;
import free.ytalerts.app.gui.businessobjects.domain.AdFirebase;

public class SplashScreenActivity extends AppCompatActivity {

    Query mAd;
    ValueEventListener valueEventListener;
    ValueEventListener singleValueEventListener;

    ArrayList<AdFirebase> ads;

    Intent intent;

    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final ImageView imageView = (ImageView) findViewById(R.id.image_splash_screen);
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.no_connection_container);
        Button btNoConnection = (Button) findViewById(R.id.btn_try_again);
        btNoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {

                    imageView.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);

                    intent = new Intent(SplashScreenActivity.this, MainActivity.class);

                    if (getIntent().getExtras() != null) {
                        for (String key : getIntent().getExtras().keySet()) {

                            if (key.equals(FirebaseHelper.FIREBASE_NOTIFICATION_LINK)) {
                                value = getIntent().getExtras().getString(key);
                            }
                        }
                    }

                    setupFirebaseAd();
                }
            }
        });

        if(isConnected()) {

            imageView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            intent = new Intent(SplashScreenActivity.this, MainActivity.class);

            if (getIntent().getExtras() != null) {
                for (String key : getIntent().getExtras().keySet()) {

                    if (key.equals(FirebaseHelper.FIREBASE_NOTIFICATION_LINK)) {
                        value = getIntent().getExtras().getString(key);
                    }
                }
            }

            setupFirebaseAd();

            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }else{
            imageView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAd.removeEventListener(valueEventListener);
        mAd.removeEventListener(singleValueEventListener);
    }

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public void setupFirebaseAd(){

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mAd = mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD);

        ads = new ArrayList<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AdFirebase ad;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ad = new AdFirebase();

                    ad.setChild(postSnapshot.getKey());
                    ad.setChannel((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_CHANNEL).getValue());
                    ad.setDescription((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_DESCRIPTION).getValue());
                    ad.setBanner((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_BANNER).getValue());
                    ad.setClicks((Long) postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_CLICKS).getValue());
                    ad.setId_channel((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_ID_CHANNEL).getValue());
                    ad.setImpressions((Long) postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_IMPRESSIONS).getValue());

                    ads.add(ad);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SplashScreenActivity.this, R.string.error_loading_ad, Toast.LENGTH_LONG).show();
            }
        };

        singleValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                Collections.shuffle(ads);

                mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD)
                        .child(ads.get(0).getChild())
                        .child(FirebaseHelper.FIREBASE_DATABASE_IMPRESSIONS)
                        .setValue(ads.get(0).getImpressions()+1);

                FirebaseHelper.NAME_CHANNEL = ads.get(0).getChannel();
                FirebaseHelper.DESCRIPTION = ads.get(0).getDescription();
                FirebaseHelper.BANNER = ads.get(0).getBanner();
                FirebaseHelper.CLICKS = ads.get(0).getClicks();
                FirebaseHelper.ID_CHANNEL= ads.get(0).getId_channel();
                FirebaseHelper.IMPRESSIONS = ads.get(0).getImpressions();
                startActivity(intent);

                if(value!=null) {
                    Intent i = new Intent(SplashScreenActivity.this, YouTubePlayerActivity.class);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.youtube.com/watch?v="+value));
                    startActivity(i);
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SplashScreenActivity.this, R.string.error_loading_ad, Toast.LENGTH_LONG).show();
            }
        };

        mAd.addValueEventListener(valueEventListener);

        mAd.addListenerForSingleValueEvent(singleValueEventListener);
    }
}
