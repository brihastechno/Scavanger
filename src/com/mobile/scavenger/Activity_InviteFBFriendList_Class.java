package com.mobile.scavenger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.shop.JSON.JSONParserGet;
import com.shop.JSON.JSONParserPost;

public class Activity_InviteFBFriendList_Class extends Activity {
	private static final String[] PERMS = new String[] { "publish_stream",
			"read_stream", "offline_access" };
	public static final String APP_ID = "405265042889545";
	private ArrayList<String> arraylist_friends_id;
	private final ArrayList<Friend_Users> arraylist_app_all_users = new ArrayList<Friend_Users>();
	private final ArrayList<String> arraylist_friends_installed_id = new ArrayList<String>();

	private LazyAdapter lazyadapter;
	private ListView listView;
	private ProgressDialog mSpinner;
	private AsyncFacebookRunner mAsyncRunner;
	private Facebook mFacebook;
	private Context mContext;
	ArrayList<Bitmap> friend_pic = new ArrayList<Bitmap>();

	private SharedPreferences sharedPrefs;
	public static final String TAG = "FACEBOOK";
	ImageView image_back;
	Button button_invite;
	public static ArrayList<Boolean> sparseBooleanArra;
	String[] friends_name, friends_id;
	ArrayList<String> friends_invitation_id, friends_invitation_name;
	ArrayList<String> friends_fb_invite_install, friends_fb_notinstall;
	String Creator_id, Creator_name;
	Typeface robot_medium;
	TextView text_message_title;
	String Graph_Check;
	String select_all_Query = "SELECT uid,name FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) ORDER BY name";
	String select_application = "SELECT uid,is_app_user FROM user WHERE is_app_user AND uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";
	private static final String GAME_INVITATION_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/new-game.php";
	private static final String APP_ALL_USERS = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification_dev/user_request.php?";
	private static final String TAG_RESULTS = "result";
	private static final String TAG_NAME = "name";
	private static final String TAG_FB_ID = "fb_id";
	private static final String TAG_PROFILE_PIC = "profile_pic";

	// private static final String GAME_INVITATION_API =
	// "	http://brstdev.com/scavanger/gcm_send_notification/new-game.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitefbfriendslist_layout);
		Creator_id = getIntent().getStringExtra("Fb_Id");
		Creator_name = getIntent().getStringExtra("Fb_Name");
		robot_medium = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		text_message_title = (TextView) findViewById(R.id.textView_message);
		listView = (ListView) findViewById(R.id.friendsview);
		image_back = (ImageView) findViewById(R.id.image_back);
		button_invite = (Button) findViewById(R.id.button_invite);
		lazyadapter = new LazyAdapter(Activity_InviteFBFriendList_Class.this,
				arraylist_app_all_users);
		listView.setAdapter(lazyadapter);
		arraylist_friends_id = new ArrayList<String>();
		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		text_message_title.setTypeface(robot_medium);
		button_invite.setTypeface(robot_medium);
		mSpinner.setMessage("Loading...");
		mSpinner.show();
		setConnection();
		getID(mSpinner);
		image_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		button_invite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				friends_fb_invite_install = new ArrayList<String>();
				friends_fb_notinstall = new ArrayList<String>();
				friends_invitation_id = new ArrayList<String>();
				friends_invitation_name = new ArrayList<String>();
				sparseBooleanArra = LazyAdapter.sparseBooleanArray;
				for (int i = 0; i < sparseBooleanArra.size(); i++) {
					Friend_Users f = arraylist_app_all_users.get(i);
					if (sparseBooleanArra.get(i).booleanValue()) {
						friends_invitation_id.add(f.id);
						friends_invitation_name.add(f.name);
					}
				}
				friends_name = new String[friends_invitation_name.size()];
				friends_id = new String[friends_invitation_name.size()];
				for (int k = 0; k < friends_invitation_id.size(); k++) {
					friends_name[k] = friends_invitation_name.get(k);
					friends_id[k] = friends_invitation_id.get(k);

				}
				if (friends_name.length > 0) {
					for (int i = 0; i < friends_name.length; i++) {

						if (arraylist_friends_id.contains(friends_id[i])) {
							friends_fb_invite_install.add(friends_id[i]);
							// st2.append(friends_id[i]);
							// st2.append(",");
						} else {
							friends_fb_notinstall.add(friends_id[i]);

						}

					}

					if (isSession()) {

						new ProgressTaskFacebookUpdates(
								Activity_InviteFBFriendList_Class.this)
								.execute();
					}

				} else {
					show_toast("No Friends Selected");
				}

			}
		});

	}

	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}

	public void getID(ProgressDialog progbar) {
		mSpinner = progbar;
		if (isSession()) {
			new Progress_Fetch_From_Server(
					Activity_InviteFBFriendList_Class.this).execute();

		} else {
			show_toast("Please Login Again");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class FriendsRequestListener implements
			com.facebook.android.AsyncFacebookRunner.RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			Log.e("Resonse Vlaueedd faxcebook ", "" + response);
			mSpinner.dismiss();
			try {
				final JSONObject json = new JSONObject(response);
				JSONArray json_array = json.getJSONArray("data");
				int length_array = (json_array != null ? json_array.length()
						: 0);

				for (int i = 0; i < length_array; i++) {
					JSONObject o = json_array.getJSONObject(i);
					String name_friend = o.getString("name");
					String id = o.getString("uid");

					if (arraylist_friends_id.contains(id)) {
						int index = arraylist_friends_id.indexOf(id);
						Friend_Users app_user = arraylist_app_all_users
								.get(index);
						app_user.installed = 1;
					}

				}

				// Only the original owner thread can touch its views
				Activity_InviteFBFriendList_Class.this
						.runOnUiThread(new Runnable() {
							public void run() {
								lazyadapter = new LazyAdapter(
										Activity_InviteFBFriendList_Class.this,
										arraylist_app_all_users);
								listView.setAdapter(lazyadapter);
								lazyadapter.notifyDataSetChanged();

							}
						});

			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			mSpinner.dismiss();
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			mSpinner.dismiss();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			mSpinner.dismiss();
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			mSpinner.dismiss();
		}
	}

	public class Friends_Installed_Application implements
			com.facebook.android.AsyncFacebookRunner.RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			arraylist_friends_installed_id.clear();
			try {

				final JSONObject json = new JSONObject(response);
				JSONArray json_array = json.getJSONArray("data");
				int length_array = (json_array != null ? json_array.length()
						: 0);

				for (int i = 0; i < length_array; i++) {
					JSONObject o = json_array.getJSONObject(i);
					String id = o.getString("uid");
					arraylist_friends_installed_id.add(id);

				}
				// Only the original owner thread can touch its views
				Activity_InviteFBFriendList_Class.this
						.runOnUiThread(new Runnable() {
							public void run() {
								Bundle selct_all_friends = new Bundle();
								Object ob = new Object();

								selct_all_friends.putString("q",
										select_all_Query);
								mAsyncRunner
										.request("/fql", selct_all_friends,
												"GET",
												new FriendsRequestListener(),
												ob);

							}
						});

			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}
	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_InviteFBFriendList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTask(Activity_InviteFBFriendList_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_toAPI();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			show_toast("Game Invitation sent");
			dialog.cancel();
			finish();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	// Call the above class using this:
	public class ProgressTaskFacebookUpdates extends
			AsyncTask<String, Void, String> {
		Activity_InviteFBFriendList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskFacebookUpdates(
				Activity_InviteFBFriendList_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			for (int i = 0; i < friends_fb_notinstall.size(); i++) {
				if (friends_fb_notinstall.get(i).length() <= 1) {

				} else {

					Bundle postParams = new Bundle();
					postParams
							.putString(
									"message",
									Creator_name
											+ " has invited you to play Mobile Scavenger - it's scavenger hunt with an attitude. Click on this link, https://play.google.com/store/apps/details?id=com.mobile.scavenger");

					mAsyncRunner.request(
							friends_fb_notinstall.get(i) + "/feed", postParams,
							"POST", new RequestListener() {
								@Override
								public void onMalformedURLException(
										MalformedURLException e, Object state) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onIOException(IOException e,
										Object state) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onFileNotFoundException(
										FileNotFoundException e, Object state) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onFacebookError(FacebookError e,
										Object state) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onComplete(String response,
										Object state) {
									// TODO Auto-generated method stub

								}
							}, null);

				}
			}

			send_data_toAPI();
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			show_toast("Game Invitation sent");

			dialog.cancel();
			finish();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	private void send_data_toAPI() {

		String url = GAME_INVITATION_API.replaceAll(" ", "");
		StringBuilder st1 = new StringBuilder();
		StringBuilder st2 = new StringBuilder();
		for (int i = 0; i < friends_name.length; i++) {
			st1.append(friends_name[i]);
			st2.append(friends_id[i]);
			if (i == friends_name.length - 1) {

			} else {
				st1.append(",");
				st2.append(",");
			}
		}

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("Creator_id", Creator_id));
		nameValuePairs.add(new BasicNameValuePair("Friend_id", st2.toString()));
		nameValuePairs
				.add(new BasicNameValuePair("Friend_name", st1.toString()));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json3 = jParser.getJSONFromUrl(url, nameValuePairs);
		Bundle params = new Bundle();
		params.putString("message", "Hey there!");
		params.putString("to", Creator_id);

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoader image = new ImageLoader(getApplicationContext());
		image.clearCache();
	}

	// Call the above class using this:
	public class Progress_Fetch_From_Server extends
			AsyncTask<String, Void, String> {
		Activity_InviteFBFriendList_Class activity_login;

		public Progress_Fetch_From_Server(
				Activity_InviteFBFriendList_Class login) {
			activity_login = login;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			get_all_users();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				// Only the original owner thread can touch its views
				Activity_InviteFBFriendList_Class.this
						.runOnUiThread(new Runnable() {
							public void run() {
								Bundle selct_all_friends = new Bundle();
								Object ob = new Object();

								selct_all_friends.putString("q",
										select_all_Query);
								mAsyncRunner
										.request("/fql", selct_all_friends,
												"GET",
												new FriendsRequestListener(),
												ob);

							}
						});
			} catch (FacebookError e) {

			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

	}

	private void get_all_users() {
		String url2 = APP_ALL_USERS + "fb_id=" + Creator_id;
		String url = url2.replaceAll(" ", "");

		// Creating JSON Parser instance
		JSONParserGet jParser = new JSONParserGet();
		// getting JSON string from URL
		String response = jParser.getJSONFromUrl(url);
		JSONObject json;

		try {

			json = new JSONObject(response);
			// Getting Array of Contacts
			JSONArray result_array = json.getJSONArray(TAG_RESULTS);

			for (int i = 0; i < result_array.length(); i++) {
				JSONObject json2 = result_array.getJSONObject(i);
				String name_friend = json2.getString(TAG_NAME);
				String id = json2.getString(TAG_FB_ID);
				Friend_Users app_users = new Friend_Users();
				app_users.id = id;
				app_users.name = name_friend;
				app_users.pic_url = "http://graph.facebook.com/" + id
						+ "/picture?width=100&height=100";
				app_users.installed = 0;
				arraylist_friends_id.add(id);

				arraylist_app_all_users.add(app_users);

			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
