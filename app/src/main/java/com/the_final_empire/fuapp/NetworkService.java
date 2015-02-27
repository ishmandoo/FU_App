package com.the_final_empire.fuapp;

//import android.app.DownloadManager;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Arrays;
import java.util.HashSet;


public class NetworkService extends IntentService {
    private static String TAG = NetworkService.class.getSimpleName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String ACTION_LOG_IN = "com.the_final_empire.fuapp.action.log_in";
    private static final String ACTION_UPDATE_FRIENDS = "com.the_final_empire.fuapp.action.update_friends";
    private static final String ACTION_FUCK = "com.the_final_empire.fuapp.action.FUCK";


    private static final String EXTRA_USERNAME = "com.the_final_empire.fuapp.extra.USERNAME";
    private static final String EXTRA_PASSWORD = "com.the_final_empire.fuapp.extra.PASSWORD";
    public static final String EXTRA_FRIEND_ARRAY = "com.the_final_empire.fuapp.extra.FRIEND_ARRAY";
    public static final String EXTRA_FRIEND_NAME = "com.the_final_empire.fuapp.extra.FRIEND_NAME";


    public static final String BROADCAST_LOGGED_IN = "com.the_final_empire.fuapp.broadcast.LOGGED_IN";
    public static final String BROADCAST_FRIEND_UPDATE = "com.the_final_empire.fuapp.broadcast.FRIEND_UPDATE";

    private static OkHttpClient mClient= new OkHttpClient();
    private static CookieManager mCookieManager = new CookieManager();

    private String regid;
    private String SENDER_ID = "1070363014238";

    private final Gson mGson = new Gson();


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private GoogleCloudMessaging gcm;

    @Override
    public void onCreate() {
        gcm = GoogleCloudMessaging.getInstance(this);
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        mClient.setCookieHandler(mCookieManager);
        super.onCreate();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionLogIn(Context context, String username, String password) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_LOG_IN);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateFriends(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_UPDATE_FRIENDS);
        context.startService(intent);
    }

    public static void startActionFUCK(Context context, String friendName) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_FUCK);
        intent.putExtra(EXTRA_FRIEND_NAME, friendName);
        context.startService(intent);
    }

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOG_IN.equals(action)) {
                final String username = intent.getStringExtra(EXTRA_USERNAME);
                final String password = intent.getStringExtra(EXTRA_PASSWORD);
                try {
                    handleActionLogIn(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_UPDATE_FRIENDS.equals(action)) {
                try {
                    handleActionUpdateFriends();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_FUCK.equals(action)) {
                try {
                    final String friendName = intent.getStringExtra(EXTRA_FRIEND_NAME);
                    handleActionFUCK(friendName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLogIn(String username, String password) throws IOException {
        if (logIn(username, password)){
            register();
            Intent loggedIn = new Intent(BROADCAST_LOGGED_IN);
            this.sendBroadcast(loggedIn);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateFriends() throws IOException {
        updateFriends();
    }

    void updateFriends() throws IOException {
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/friends")
                .get()
                .build();
        Response response = mClient.newCall(request).execute();

        String[] friendArray = mGson.fromJson(response.body().string(), String[].class);
        Intent friendUpdate = new Intent(BROADCAST_FRIEND_UPDATE);
        friendUpdate.putExtra(EXTRA_FRIEND_ARRAY, friendArray);
        this.sendBroadcast(friendUpdate);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet("friends", new HashSet<String>(Arrays.asList(friendArray)));
        editor.commit();




    }

    private void handleActionFUCK(String friendName) throws IOException {
        fu(friendName);
    }

    boolean logIn(final String username, final String password) throws IOException {
        if (!checkLogIn()) {
            Log.i(TAG, "Log in function start " + username);
            RequestBody formBody = new FormEncodingBuilder()
                    .add("username", username)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url("http://quiet-taiga-6899.herokuapp.com/login")
                    .post(formBody)
                    .build();
            Response response = mClient.newCall(request).execute();

            if (response.isSuccessful()) {

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

                SharedPreferences.Editor editor = sharedPrefs.edit();
                Log.i(TAG, "Login successful " + username);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("is_user_saved", true);
                editor.commit();

                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }


    }

    boolean checkLogIn() throws IOException{
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/loggedIn")
                .get()
                .build();
        Response response = mClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return true;
        } else {
            return false;
        }
    }


    private void register() throws IOException {
        Log.i(TAG, "Register function start");

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                regid = gcm.register(SENDER_ID);


                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("regid", regid);
                editor.commit();

                registerId();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

    }

    private String getRegistrationId() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String registrationId = prefs.getString("regid", "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    void registerId() throws IOException{
        RequestBody body = RequestBody.create(JSON, "{\"regId\":\"" + regid + "\"}");
        Log.i(TAG, "{\"regId\":\"" + regid + "\"}");
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/android/register")
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();

        if (response.isSuccessful()) {
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }



    void fu(String name) throws IOException {
        Log.i(TAG, name);
        RequestBody body = RequestBody.create(JSON, "{\"friendName\":\"" + name + "\"}");
        Log.i(TAG, "{\"friendName\":\"" + name + "\"}");
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/fu")
                .post(body)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "Failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i(TAG, response.body().string());
                if (response.isSuccessful()) {
                    startActionUpdateFriends(getBaseContext());
                }
                Log.i(TAG, String.valueOf(mCookieManager.getCookieStore().getCookies()));
            }
        });
    }

}
