package free.rm.skytube.gui.activities;

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

import free.rm.skytube.R;
import free.rm.skytube.gui.businessobjects.FirebaseHelper;

public class SplashScreenActivity extends AppCompatActivity {

    Query mAd;
    ValueEventListener valueEventListener;
    ValueEventListener singleValueEventListener;

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

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    FirebaseHelper.NAME_CHANNEL = (String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_CHANNEL).getValue();
                    FirebaseHelper.DESCRIPTION = (String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_DESCRIPTION).getValue();
                    FirebaseHelper.BANNER = (String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_BANNER).getValue();
                    FirebaseHelper.CLICKS = (Long) postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_CLICKS).getValue();
                    FirebaseHelper.ID_CHANNEL= (String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_ID_CHANNEL).getValue();
                    FirebaseHelper.IMPRESSIONS = (Long) postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_IMPRESSIONS).getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SplashScreenActivity.this, R.string.error_loading_ad, Toast.LENGTH_LONG).show();
            }
        };

        singleValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
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
