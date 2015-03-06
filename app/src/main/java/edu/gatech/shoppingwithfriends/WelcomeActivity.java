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


public class WelcomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Firebase.setAndroidContext(this); //Initialize Firebase

        //Create Listener for Register Button on Home Screen
        Button registerButton = (Button) findViewById(R.id.welcome_registerbutton);

        registerButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });

        //Create Listener for Login Button on Home Screen
        Button loginButton = (Button) findViewById(R.id.loginbutton);

        loginButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(v.getContext(), LoginActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void startLoginScreen(View v){
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }
}
