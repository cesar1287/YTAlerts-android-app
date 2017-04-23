package free.ytalerts.app.gui.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import free.ytalerts.app.R;
import free.ytalerts.app.gui.businessobjects.firebase.FirebaseHelper;

public class AboutActivity extends AppCompatActivity {

    Query about;
    ValueEventListener valueEventListener;
    ValueEventListener singleValueEventListener;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        dialog = ProgressDialog.show(this,"", this.getResources().getString(R.string.loading_about_pls_wait), true, false);

        setupAbout();

        // display the back button in the action bar (left-hand side)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        about.removeEventListener(valueEventListener);

        about.removeEventListener(singleValueEventListener);
    }

    private void setupAbout() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        about = mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_ABOUT);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    TextView emailIcon = (TextView) findViewById(R.id.email_developer_icon);
                    emailIcon.setText((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_EMAIL_ICON).getValue());

                    TextView emailYTAlerts = (TextView) findViewById(R.id.email_proper_ytalerts);
                    emailYTAlerts.setText((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_EMAIL_YTALERTS).getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AboutActivity.this, R.string.error_loading_emails_about, Toast.LENGTH_LONG).show();
            }
        };

        singleValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AboutActivity.this, R.string.error_loading_emails_about, Toast.LENGTH_LONG).show();
                finish();
            }
        };

        about.addValueEventListener(valueEventListener);

        about.addListenerForSingleValueEvent(singleValueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // when the user clicks the back/home button...
            case android.R.id.home:
                // close this activity
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
