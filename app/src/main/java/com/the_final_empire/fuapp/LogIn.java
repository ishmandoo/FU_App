package com.the_final_empire.fuapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class LogIn extends ActionBarActivity {
    public static String TAG = LogIn.class.getSimpleName();

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;

    private IntentFilter mLoggedInIntentFilter;

    private SharedPreferences mSharedPref;



    private BroadcastReceiver mLoggedInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Logged In");
            Intent startMain = new Intent(getBaseContext(), FUActivity.class);
            startActivity(startMain);


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mSharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String saved_username = mSharedPref.getString("username", "");
        String saved_password = mSharedPref.getString("password", "");
        Boolean is_user_saved =  mSharedPref.getBoolean("is_user_saved", false);

        mLoggedInIntentFilter = new IntentFilter(NetworkService.BROADCAST_LOGGED_IN);
        registerReceiver(mLoggedInReceiver, mLoggedInIntentFilter);

        Log.i(TAG, "Automatic login with username " + saved_password);


        if (is_user_saved) {
            NetworkService.startActionLogIn(getBaseContext(), saved_username, saved_password);
        } else {

            setContentView(R.layout.activity_log_in);

            mUsername = (EditText) findViewById(R.id.username);
            mPassword = (EditText) findViewById(R.id.password);
            mLoginButton = (Button) findViewById(R.id.logInButton);

            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkService.startActionLogIn(getBaseContext(), mUsername.getText().toString(), mPassword.getText().toString());
                }
            });
        }
    }

    @Override
    protected void onStart(){
        mLoggedInIntentFilter = new IntentFilter(NetworkService.BROADCAST_LOGGED_IN);
        registerReceiver(mLoggedInReceiver, mLoggedInIntentFilter);
        super.onStart();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mLoggedInReceiver);
        super.onPause();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
