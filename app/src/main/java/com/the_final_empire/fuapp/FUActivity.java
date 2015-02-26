package com.the_final_empire.fuapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Network;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class FUActivity extends ActionBarActivity {
    public static String TAG = FUActivity.class.getSimpleName();

    private ListView mFriendList;
    private ArrayAdapter<String> mFriendAdapter;

    private IntentFilter mFriendUpdateIntentFilter = new IntentFilter(NetworkService.BROADCAST_FRIEND_UPDATE);;

    private BroadcastReceiver mFriendUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] friendArray = intent.getStringArrayExtra(NetworkService.EXTRA_FRIEND_ARRAY);
            mFriendAdapter.clear();
            for (String friend : friendArray) {
                Log.i(TAG, "FRIEND: " + friend);
                mFriendAdapter.add(friend);
            }
            mFriendAdapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu);

        Log.i(TAG, "Register function start");

        mFriendList = (ListView) findViewById(R.id.friendList);
        mFriendAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>());
        mFriendList.setAdapter(mFriendAdapter);




        NetworkService.startActionUpdateFriends(this);


        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String friendName = ((TextView) view).getText().toString();

                NetworkService.startActionFUCK(getApplicationContext(), friendName);


            }
        });
    }

    @Override
    protected void onStart() {
        registerReceiver(mFriendUpdateReceiver, mFriendUpdateIntentFilter);
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        Set<String> friends = sharedPrefs.getStringSet("friends", new HashSet<String>());

        if(friends != null){
            mFriendAdapter.clear();
            Iterator<String> iterator = friends.iterator();

            while(iterator.hasNext()){
                String friend = iterator.next();
                mFriendAdapter.add(friend);
            }
            mFriendAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onStop() {
        unregisterReceiver(mFriendUpdateReceiver);
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fu, menu);
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
