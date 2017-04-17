package free.rm.skytube.gui.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import free.rm.skytube.R;
import free.rm.skytube.gui.businessobjects.ExpandableListAdapter;
import free.rm.skytube.gui.businessobjects.FirebaseHelper;
import free.rm.skytube.gui.businessobjects.domain.DoubtFirebase;

public class DoubtsActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    Query doubt;

    ValueEventListener valueEventListener;
    ValueEventListener singleValueEventListener;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts);

        // display the back button in the action bar (left-hand side)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.doubts);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        dialog = ProgressDialog.show(this,"", this.getResources().getString(R.string.loading_doubts_pls_wait), true, false);

        // preparing list data
        prepareListData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doubt.removeEventListener(valueEventListener);
        doubt.removeEventListener(singleValueEventListener);
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

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        doubt = mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_DOUBTS);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DoubtFirebase doubtFirebase;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    doubtFirebase = new DoubtFirebase();
                    doubtFirebase.setPergunta((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_QUESTION).getValue());
                    doubtFirebase.setResposta((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_ANSWER).getValue());

                    listDataHeader.add(doubtFirebase.getPergunta());

                    List<String> answer = new ArrayList<>();
                    answer.add(doubtFirebase.getResposta());

                    listDataChild.put(listDataHeader.get(listDataHeader.size()-1),answer);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DoubtsActivity.this, R.string.error_loading_doubts, Toast.LENGTH_LONG).show();
                finish();
            }
        };

        singleValueEventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                listAdapter = new ExpandableListAdapter(DoubtsActivity.this, listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DoubtsActivity.this, R.string.error_loading_doubts, Toast.LENGTH_LONG).show();
                finish();
            }
        };

        doubt.addValueEventListener(valueEventListener);

        doubt.addListenerForSingleValueEvent(singleValueEventListener);
    }
}
