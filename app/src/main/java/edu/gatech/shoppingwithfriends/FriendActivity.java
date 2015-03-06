package edu.gatech.shoppingwithfriends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2/12/2015.
 */
public class FriendActivity extends Activity {


    protected static ArrayList<String> outList = new ArrayList<String>();

    private EditText mEmail; // Username entered into the username field
    private ListView mFriendList; // List of friends to be used by listview
    private List<String> friendslist; // Friend list
    private ArrayAdapter<String> adapter;

    private String email;

    private Context curContext;
    private int pos;

    /**
     * Method that is run when activity is initiated.
     * Contains information on any prior saved state.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        curContext = this.getApplicationContext();

        mEmail = (EditText) findViewById(R.id.username);

        mFriendList = (ListView) findViewById(R.id.listView);

        friendslist = (List) CurrentUser.getFriendNames().clone();

        // makes use of list view
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendslist);

        mFriendList.setAdapter(adapter);

        //setting listener for click to navigate to detail view
        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailView(position);
            }
        });


        //Setting listener for long click to delete friends
        mFriendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * Detects a long click to delete a friend from the friend list view
             * @param adapter1 unused adapter for longClick method
             * @param v current view
             * @param position the position in the listview that was clicked
             * @param id the id of the listview clicked
             * @return boolean true if long click occurs
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter1, View v, int position, long id) {
                //on long click, show dialog box to confirm deletion
                pos = position; // to allow access to position variable from inner classes
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);

                builder.setTitle("Confirm Friend Deletion");
                builder.setMessage("Are you sure you want to remove " + CurrentUser.getFriendNames().get(pos) + " as a friend?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //remove friend locally
                        String friendremoved = CurrentUser.removeFriend(CurrentUser.getFriends().get(pos));
                        //Update backend firebase with deleted friend
                        String mEmailNoPunc = CurrentUser.getEmail().replaceAll("\\W", "");
                        Firebase emailsRef = new Firebase(FirebaseSingleton.getUserDataURL() + "/" + mEmailNoPunc);
                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("friends", CurrentUser.getFriends());
                        updates.put("friendsnames", CurrentUser.getFriendNames());
                        emailsRef.updateChildren(updates);
                        adapter.clear();
                        adapter.addAll(CurrentUser.getFriendNames());
                        adapter.notifyDataSetChanged();
                        mFriendList.requestFocus();
                    }

                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });


        Button mAdd = (Button) findViewById(R.id.addButton);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //boolean valid = isSearchValid();
                boolean add = true;
                email = mEmail.getText().toString();

                // check for addition of an already added friend
                if (LoginActivity.isEmailValid(email)) {
                    if (email.equalsIgnoreCase(CurrentUser.getEmail())) {
                        mEmail.setError("You cannot add yourself as a friend!");
                    } else if (CurrentUser.getFriends().contains(email)) {
                        mEmail.setError("You are already friends!");
                    } else {
                        String mEmailNoPunc = email.replaceAll("\\W", "");
                        Firebase ref = new Firebase(FirebaseSingleton.getUserDataURL() + "/" + mEmailNoPunc);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DataSnapshot dsFName = snapshot.child("/firstname");
                                    DataSnapshot dsLName = snapshot.child("/lastname");
                                    String fName = (String) dsFName.getValue();
                                    String lName = (String) dsLName.getValue();
                                    Log.v("FriendActivity", "Friend info: " + fName + lName + email);
                                    String newfriend = CurrentUser.addFriend(email, fName, lName);
                                    Log.v("FriendActivity", "Current User Friends: " + CurrentUser.getFriendNames());
                                    friendslist.add(newfriend);
                                    Log.v("FriendActivity", "Current User Friends: " + CurrentUser.getFriendNames());

                                    String mEmailNoPunc = CurrentUser.getEmail().replaceAll("\\W", "");
                                    Firebase emailsRef = new Firebase(FirebaseSingleton.getUserDataURL() + "/" + mEmailNoPunc);
                                    Map<String, Object> updates = new HashMap<String, Object>();
                                    updates.put("friends", CurrentUser.getFriends());
                                    updates.put("friendsnames", CurrentUser.getFriendNames());
                                    emailsRef.updateChildren(updates);
                                    adapter.notifyDataSetChanged();
                                    mEmail.clearFocus();
                                    mEmail.setText("");

                                    //hide virtual keyboard
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);
                                } else {
                                    Log.v("FriendActivity", "No user registered for that email address");
                                    mEmail.setError("No user registered for that email address");
                                    mEmail.requestFocus();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.v("FriendActivity", "An error occurred fetching if email is valid!");
                                mEmail.setError("Error occurred fetching data");
                            }
                        });
                    }
                }
            }
        });

    }
    /**
     * Method Called when item in listView is clicked
     * creates intent and takes user to friend detail screen
     *
     * @param position
     */
    private void goToDetailView(int position) {
        Intent i = new Intent(this, FriendDetailActivity.class);
        i.putExtra("position", position);
        startActivity(i);
    }
}
