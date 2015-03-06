package edu.gatech.shoppingwithfriends;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers register.
 */
public class RegisterActivity extends LoginActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUserView;
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mFNameView;
    private AutoCompleteTextView mLNameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private static boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mUserView = (AutoCompleteTextView) findViewById(R.id.user);
        mFNameView = (AutoCompleteTextView) findViewById(R.id.firstname);
        mLNameView = (AutoCompleteTextView) findViewById(R.id.lastname);

        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirmpassword);
        /*mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });*/

        Button mUserRegisterButton = (Button) findViewById(R.id.register_button);
        mUserRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);
        mFNameView.setError(null);
        mLNameView.setError(null);
        mConfirmPasswordView.setError(null);
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserView.getText().toString();
        String email = mEmailView.getText().toString();
        String firstname = mFNameView.getText().toString();
        String lastname = mLNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmpassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid user
        if (TextUtils.isEmpty(username) || !isUsernameValid(username)) {
            mUserView.setError(getString(R.string.error_invalid));
            focusView = mUserView;
            cancel = true;
        }

        // Check for a valid first name
        if (TextUtils.isEmpty(firstname)) {
            mFNameView.setError(getString(R.string.error_invalid));
            focusView = mFNameView;
            cancel = true;
        }

        // Check for a valid last name
        if (TextUtils.isEmpty(lastname)) {
            mLNameView.setError(getString(R.string.error_invalid));
            focusView = mLNameView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid confirm password
        if (TextUtils.isEmpty(confirmpassword) || !isPasswordValid(confirmpassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_invalid));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        //check if passwords match
        if (!password.equals(confirmpassword)) {
            mPasswordView.setError("Passwords do not match!");
            focusView = mPasswordView;
            cancel = true;
            mPasswordView.clearComposingText();
            mConfirmPasswordView.clearComposingText();
        }


        // Check for a valid username address.
        /*if (TextUtils.isEmpty(username)) {
            mUserView.setError(getString(R.string.error_field_required)); //If nothing entered for username
            focusView = mUserView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUserView.setError(getString(R.string.error_invalid_user)); //if username is valid
            focusView = mUserView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required)); //if nothing entered for password
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(username)) {
            mPasswordView.setError(getString(R.string.error_invalid_password)); //if password is invalid
            focusView = mPasswordView;
            cancel = true;
        }
        //See if username already exists
        for (String credential : credentials) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(username)) {
                mUserView.setError(getString(R.string.error_user_exists));
                focusView = mUserView;
                cancel = true;
            }
        }*/

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(username, email, firstname, lastname, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only username addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> usernames = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            usernames.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addUsernamesToAutoComplete(usernames);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addUsernamesToAutoComplete(List<String> usernameAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, usernameAddressCollection);

        //mUserView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mEmail;
        private final String mFName;
        private final String mLName;

        UserRegisterTask(String username, String password) {
            mUsername = username;
            mPassword = password;
            mEmail = null;
            mFName = null;
            mLName = null;
        }

        UserRegisterTask(String username, String email, String fName, String lName, String password) {
            mUsername = username;
            mPassword = password;
            mEmail = email;
            mFName = fName;
            mLName = lName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            //credentials.add(mUsername + ":" + mPassword);
            final UserRegisterTask currentTask = this;
            showProgress(true);
            Firebase ref = new Firebase(FirebaseSingleton.getUserAuthURL());
            ref.createUser(mEmail, mPassword, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    // user was created with credentials
                    RegisterActivity.success = true;
                    Log.v("RegisterActivity", "Credentials added: " + mUsername + ":" + mPassword);

                    mPasswordView.setError(null);
                    mUserView.setError(null);
                    mEmailView.setError(null);
                    mLNameView.setError(null);
                    mFNameView.setError(null);
                    mConfirmPasswordView.setError(null);

                    //setup profile and relevant information
                    Firebase emailsRef = new Firebase(FirebaseSingleton.getUserDataURL());
                    String mEmailNoPunc = mEmail.replaceAll("\\W", "");
                    emailsRef.child(mEmailNoPunc).child("firstname").setValue(mFName);
                    emailsRef.child(mEmailNoPunc).child("lastname").setValue(mLName);
                    emailsRef.child(mEmailNoPunc).child("username").setValue(mUsername);
                    emailsRef.child(mEmailNoPunc).child("email").setValue(mEmail);
                    emailsRef.child(mEmailNoPunc).child("friends").setValue(CurrentUser.getFriends());
                    CurrentUser.updateUser(mEmail, mUsername, mFName, mLName);
                    CurrentUser.setLoggedIn(true);

                    Log.v("RegisterActivity", mEmail + " " + mUsername + " " + mFName + " " + mLName);

                    finish();
                    Log.v("RegisterActivity", "Starting next task");
                    //if register success - enter applications
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FromRegisterLogin", true);
                    startActivity(intent);
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    //there was an error
                    showProgress(false);
                    RegisterActivity.success = false;
                    Log.v("RegisterActivity", "Credential addition failed " + firebaseError.getMessage());

                    Log.v("RegisterActivity", "Failure in onPostExecute");
                    mPasswordView.setError(getString(R.string.error_registererror));
                    mPasswordView.requestFocus();
                }
            });
            Log.v("RegisterActivity", "Ending doInBackground");


            return RegisterActivity.success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            /*mAuthTask = null;
            showProgress(false);
            Log.v("RegisterActivity", "onPostExecuting starting. success = " + RegisterActivity.success);

            if (RegisterActivity.success) {
                finish();
                Log.v("RegisterActivity", "Starting next task");
                //if register success - enter applications
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Log.v("RegisterActivity", "Failure in onPostExecute");
                mPasswordView.setError(getString(R.string.error_registererror));
                mPasswordView.requestFocus();
            }*/
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



