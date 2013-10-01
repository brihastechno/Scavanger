package com.mobile.scavenger;

import static com.androidhive.pushnotification.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.androidhive.pushnotification.CommonUtilities.EXTRA_MESSAGE;
import static com.androidhive.pushnotification.CommonUtilities.SENDER_ID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhive.pushnotification.AlertDialogManager;
import com.androidhive.pushnotification.ServerUtilities;
import com.androidhive.pushnotification.WakeLocker;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.google.android.gcm.GCMRegistrar;
import com.shop.JSON.JSONParserPost;

public class Activity_MainActivity_Class extends Activity {
	private Button button_Signin, button_Share, button_Rules;
	private AsyncFacebookRunner mAsyncRunner;
	private Context mContext;
	private Facebook mFacebook;
	private String TAG = "Activity_MainActivity_Class";
	int mAuthActivityCode;
	DialogListener mAuthDialogListener;
	int FACEBOOK_ACTIVITY = 3;
	private SharedPreferences sharedPrefs;;
	TextView txtUserName;
	private ProgressDialog mSpinner;
	private static final String[] PERMS = new String[] { "publish_stream",
			"read_stream", "offline_access" };
	public static final String APP_ID = "405265042889545";
	public static String FB_id, FB_Name, FB_Profile_pic;
	String REGISTER_USER_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification_dev/register_cal.php?";
	// String REGISTER_USER_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/register_cal.php?";

	Typeface robot_medium;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playhome_layout);
//		robot_medium = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
//		button_Signin = (Button) findViewById(R.id.button_login);
//		button_Share = (Button) findViewById(R.id.button_share);
//		button_Rules = (Button) findViewById(R.id.button_rules);
//		mSpinner = new ProgressDialog(this);
//		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		mSpinner.setMessage("Loading...");
//		button_Signin.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				boolean internet_check = isInternetOn();
//				if (internet_check) {
//					mSpinner.show();
//					setConnection();
//					getID(mSpinner);
//				} else {
//					show_toast("Check Your Internet Connection");
//				}
//			}
//		});
//		button_Rules.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent_rules_class = new Intent(
//						Activity_MainActivity_Class.this,
//						Activity_RulesApp_Class.class);
//				startActivity(intent_rules_class);
//			}
//		});
//		button_Share.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent_share_class = new Intent(
//						Activity_MainActivity_Class.this,
//						Activity_ShareApp_Class.class);
//				startActivity(intent_share_class);
//			}
//		});

	}

	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}

	public void getID(ProgressDialog progbar) {
		mSpinner = progbar;
		if (isSession()) {

			mAsyncRunner.request("me", new IDRequestListener());
		} else {
			// no logged in, so relogin
			mFacebook.authorize(this, PERMS, new LoginDialogListener());
		}
	}

	public boolean isSession() {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String access_token = sharedPrefs.getString("access_token", "x");
		Long expires = sharedPrefs.getLong("access_expires", -1);
		if (access_token != null && expires != -1) {
			mFacebook.setAccessToken(access_token);
			mFacebook.setAccessExpires(expires);
		}
		return mFacebook.isSessionValid();
	}

	private class LoginDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			String token = mFacebook.getAccessToken();
			long token_expires = mFacebook.getAccessExpires();
			sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			sharedPrefs.edit().putLong("access_expires", token_expires)
					.commit();
			sharedPrefs.edit().putString("access_token", token).commit();
			mAsyncRunner.request("me", new IDRequestListener());
		}

		@Override
		public void onFacebookError(FacebookError e) {

		}

		@Override
		public void onError(DialogError e) {

		}

		@Override
		public void onCancel() {

		}
	}

	private class IDRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			try {

				JSONObject json = Util.parseJson(response);

				FB_id = json.getString("id");
				FB_Name = json.getString("name");
				FB_Profile_pic = "http://graph.facebook.com/" + FB_id
						+ "/picture?width=100&height=100";

				SharedPreferences myPrefs = getApplicationContext()
						.getSharedPreferences("Session", MODE_PRIVATE);
				SharedPreferences.Editor prefsEditor = myPrefs.edit();
				prefsEditor.putString("face_id", FB_id);

				prefsEditor.commit();
				Activity_MainActivity_Class.this.runOnUiThread(new Runnable() {
					public void run() {
						// username.setText("Welcome: " + name+"\n ID: "+id);

						new ProgressTask(Activity_MainActivity_Class.this)
								.execute();

					}
				});
			} catch (JSONException e) {
				Log.d(TAG, "JSONException: " + e.getMessage());
			} catch (FacebookError e) {
				Log.d(TAG, "FacebookError: " + e.getMessage());
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			Log.d(TAG, "IOException: " + e.getMessage());
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			Log.d(TAG, "FileNotFoundException: " + e.getMessage());
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			Log.d(TAG, "MalformedURLException: " + e.getMessage());
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Log.d(TAG, "FacebookError: " + e.getMessage());
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// mFacebook.setStatus(mAuthDialogListener );
		try {
			mFacebook.authorizeCallback(requestCode, resultCode, data);
		} catch (NullPointerException e) {
			mSpinner.cancel();
			show_toast("Unable to Signin");

		}

	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_MainActivity_Class activity_login;

		// Dialog dialog;

		public ProgressTask(Activity_MainActivity_Class login) {
			activity_login = login;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			GCM_Call();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mSpinner.cancel();
			Intent intent_aftersignin_class = new Intent(
					Activity_MainActivity_Class.this,
					Activity_ScreenAfterSignin_Class.class);
			intent_aftersignin_class.putExtra("FB_ID", FB_id);
			intent_aftersignin_class.putExtra("FB_Name", FB_Name);
			startActivity(intent_aftersignin_class);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	}

	private void send_data_toAPI() {
		String url = REGISTER_USER_URL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("fb_id", FB_id));
		nameValuePairs.add(new BasicNameValuePair("name", FB_Name));
		nameValuePairs.add(new BasicNameValuePair("user_pic", FB_Profile_pic));
		nameValuePairs.add(new BasicNameValuePair("reg_status", "1"));
		nameValuePairs.add(new BasicNameValuePair("reg_id", Reg_id));

		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	// Asyntask

	public static String fb_id;
	public static String fb_name;
	public static String fb_profile_pic;
	public static String ID;

	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// ARE WE CONNECTED TO THE NET
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {

			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {

			return false;
		}
		return false;
	}

	public void show_toast(String string) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setTypeface(robot_medium);
		text.setText(string);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 100);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public void GCM_Call() {
		fb_id = FB_id;
		fb_name = FB_Name;
		fb_profile_pic = FB_Profile_pic;

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		Reg_id = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (Reg_id.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, SENDER_ID);

		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Toast.makeText(getApplicationContext(),
				// "Already registered with GCM", Toast.LENGTH_LONG)
				// .show();

				send_data_toAPI();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, fb_id, fb_name,
								fb_profile_pic, Reg_id);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */
			// Toast.makeText(getApplicationContext(),
			// "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(getApplicationContext());
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	String Reg_id;

}
