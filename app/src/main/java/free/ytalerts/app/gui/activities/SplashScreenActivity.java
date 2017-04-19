package free.ytalerts.app.gui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import free.ytalerts.app.gui.businessobjects.FirebaseHelper;
import free.ytalerts.app.gui.businessobjects.domain.AdFirebase;

public class SplashScreenActivity extends AppCompatActivity {

    Query mAd;
    ValueEventListener valueEventListener;
    ValueEventListener singleValueEventListener;

    ArrayList<AdFirebase> ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setupFirebaseAd();

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAd.removeEventListener(valueEventListener);
        mAd.removeEventListener(singleValueEventListener);
    }

    public void setupFirebaseAd(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mAd = mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD);

        ads = new ArrayList<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AdFirebase ad;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ad = new AdFirebase();

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

                FirebaseHelper.NAME_CHANNEL = ads.get(0).getChannel();
                FirebaseHelper.DESCRIPTION = ads.get(0).getDescription();
                FirebaseHelper.BANNER = ads.get(0).getBanner();
                FirebaseHelper.CLICKS = ads.get(0).getClicks();
                FirebaseHelper.ID_CHANNEL= ads.get(0).getId_channel();
                FirebaseHelper.IMPRESSIONS = ads.get(0).getImpressions();
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
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
