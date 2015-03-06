package edu.gatech.shoppingwithfriends;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;



/*
Changes:
    changed friend activity to extend listactivity to be able to click the list items

 */
public class FriendDetailActivity extends ActionBarActivity {

    int postings =0;
    int rating=3;
    String chosenUsername="asdf";
    String chosenEmail="asdf";
    String chosenFirstName="asdf";
    String chosenLastName="asdf";

    /**
     * Method that is run when activity is initiated.
     * Contains information on any prior saved state.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        int position=0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("position");
        }
        String emailChosen = CurrentUser.getFriends().get(position).replaceAll("\\W","");

        //used to get information from the database using the chosen Friend's email
        Firebase aref = new Firebase(FirebaseSingleton.getUserDataURL());
        aref.child(emailChosen).addValueEventListener(new ValueEventListener() {

            /**
             * Updates the detail view fields by getting the information from the
             * database
             *
             * @param snapshot
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chosenUsername = (String)snapshot.child("/username").getValue();
                chosenEmail = (String)snapshot.child("/email").getValue();
                chosenFirstName = (String)snapshot.child("/firstname").getValue();
                chosenLastName = (String)snapshot.child("/lastname").getValue();

                TextView name = (TextView)findViewById(R.id.nameText);
                TextView username = (TextView)findViewById(R.id.usernameText);
                TextView email = (TextView)findViewById(R.id.emailText);
                TextView posts = (TextView)findViewById(R.id.postsText);
                TextView ratingT = (TextView)findViewById(R.id.ratingText);

                name.setText("NAME:  "+chosenFirstName+" "+chosenLastName);
                username.setText("USERNAME:  "+chosenUsername);
                email.setText("EMAIL:  "+chosenEmail);
                posts.setText("POSTS:  "+postings);
                ratingT.setText("RATING "+rating);

            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
