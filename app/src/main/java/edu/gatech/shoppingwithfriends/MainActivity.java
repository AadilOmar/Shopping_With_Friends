package edu.gatech.shoppingwithfriends;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.firebase.client.Firebase;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Listener for friends button
        Button friendsButton = (Button) findViewById(R.id.friends);

        friendsButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                //intent.putExtra("USER_NAME",getIntent().getExtras().getString("USERNAME"));
                startActivity(intent);
            }
        });
    }

    /*
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.logout_button) {
            attemptLogout();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to logout the user.
     */
    public void attemptLogout() {
        CurrentUser.logout();
        Firebase ref = new Firebase(FirebaseSingleton.getUserAuthURL());
        ref.unauth();
        Intent logoutIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
    }

    /**
     * Called when the back button is pressed. Back button
     * behaves as normal except for when the last activity
     * the register login. The back button will have no
     * effect in that case.
     */
    @Override
    public void onBackPressed() {
        if (!getIntent().getExtras().getBoolean("FromRegisterLogin")) {
            super.onBackPressed();
        }
    }
}
