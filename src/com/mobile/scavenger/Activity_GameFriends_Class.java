package com.mobile.scavenger;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.shop.JSON.JSONARRAY;
import com.shop.JSON.JSONParserPost;
import com.shop.JSON.JSONParserPostCheck;

public class Activity_GameFriends_Class extends Activity {
	private ListView listView;
	ArrayList<String> player_name = new ArrayList<String>();
	ArrayList<String> player_picture = new ArrayList<String>();
	ArrayList<String> player_status = new ArrayList<String>();
	ArrayList<String> player_status_display = new ArrayList<String>();
	ArrayList<String> player_fb_id = new ArrayList<String>();
	ArrayList<String> player_game_start;
	ImageView image_back, image_start;
	String GAME_ID;
	public static final String APP_ID = "405265042889545";
	private static final String TAG_RESULTS = "result";
	private static final String TAG_START = "can_start";
	private static final String TAG_CREATOR = "creator_id";
	private static final String TAG_MESSGAE = "message";
	private static final String TAG_FB_ID = "fb_id";
	private static final String TAG_NAME = "name";
	private static final String TAG_STATUS = "status";
	private static final String TAG_GAMEFRIENDS = "data";
	private static final String TAG_CREATOR_NAME = "creator_name";
	private static final String TAG_NOTIFICATION = "notification";
	private static final String TAG_HUNT_ID = "id";
	private static final String TAG_HUNT_NAME = "hunt_name";
	private static final String TAG_VALUE = "hunt_value";
	static String Player_Remove_id;

	TextView text_number;
	private AsyncFacebookRunner mAsyncRunner;
	private Facebook mFacebook;
	private SharedPreferences sharedPrefs;
	private Context mContext;

	JSONArray result_array = null;
	static String Creator_ID;
	String Creator_Name, Fb_Id;
	static Typeface robot_medium;
	static Typeface robot_bold;
	TextView text_title, text_titlesec;
	String Cretor_name;
	LinearLayout notification_layout;
	TextView text_notification;
	String Game_Status;
	boolean Game_Status2;
	ArrayList<String> notification_array = new ArrayList<String>();
	String GAME_DATA_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-table.php";
	private static final String GAME_PICTURE_DETAILS_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-pic_detail_cal.php";
	String GAME_START_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/before_game_start.php";
	String NOTIFICATION_MESSAGE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/unread_message.php";
	String LEAVE_GAME_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-leave_cal.php";
	String hunt_name, hunt_value, hunt_id, fb_id_string;

	// String GAME_START_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/before_game_start.php";
	// String NOTIFICATION_MESSAGE_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/unread_message.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamefriend_layout);
		notification_array.clear();

		text_notification = (TextView) findViewById(R.id.notifica);
		notification_layout = (LinearLayout) findViewById(R.id.notification_layout);
		robot_bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		robot_medium = Typeface
				.createFromAsset(getAssets(), "robotomedium.ttf");

		text_title = (TextView) findViewById(R.id.textView_title);
		text_titlesec = (TextView) findViewById(R.id.textView_title2);
		GAME_ID = getIntent().getStringExtra("game_id");
		Fb_Id = getIntent().getStringExtra("FB_ID");
		Cretor_name = getIntent().getStringExtra("Fb_Name");
		Game_Status = getIntent().getStringExtra("Status_game");
		text_number = (TextView) findViewById(R.id.text_number);
		player_fb_id.clear();
		player_name.clear();
		player_picture.clear();
		player_status.clear();
		player_status_display.clear();
		image_back = (ImageView) findViewById(R.id.imageView1);
		image_start = (ImageView) findViewById(R.id.imageView_start);
		image_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				finish();
			}
		});
		image_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				int count = 0;
				player_game_start = new ArrayList<String>();
				for (int i = 0; i < player_status.size(); i++) {
					if (player_status.get(i).equalsIgnoreCase("1")
							|| player_status.get(i).equalsIgnoreCase("2")) {
						count++;
						if (player_fb_id.get(i).equalsIgnoreCase(Creator_ID)) {
						} else {
							player_game_start.add(player_fb_id.get(i));
						}

					}
				}

				if (count % 2 == 0) {
					if (Game_Status.equalsIgnoreCase("0")) {
						if (Creator_ID.equalsIgnoreCase(Fb_Id)) {
							setConnection();
							if (isSession()) {
								new ProgressTaskFacebookUpdates(
										Activity_GameFriends_Class.this)
										.execute();
							} else {
								show_toast("Session Expired");
							}
						} else {
							show_toast("You Cannot Start the Game");
						}
					} else {
						Intent i = new Intent(Activity_GameFriends_Class.this,
								Activity_PlayHome_Class.class);
						i.putExtra("Game_id", GAME_ID);
						i.putExtra("FB_ID", Fb_Id);
						i.putExtra("Fb_Name", Cretor_name);
						startActivity(i);
					}
				} else {
					show_toast("Game can not be Started Yet");
				}
			}
		});
		notification_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				notification_layout.setVisibility(8);
			}
		});
		text_notification.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				notification_layout.setVisibility(8);
			}
		});
		text_title.setTypeface(robot_bold);
		text_titlesec.setTypeface(robot_bold);
		text_number.setTypeface(robot_medium);
		new ProgressTask(Activity_GameFriends_Class.this).execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		notification_array.clear();
		new Progress_Notification(Activity_GameFriends_Class.this).execute();
	}

	public class MlistAdapter extends BaseAdapter {
		ArrayList<String> player_name;
		ArrayList<String> player_picture;
		ArrayList<String> player_status;
		ArrayList<String> player_Facebook_id;
		Activity_GameFriends_Class context;
		ImageLoader imageLoader;
		ArrayList<Integer> button_remove_visibility;
		private LayoutInflater inflater = null;

		public MlistAdapter(Activity_GameFriends_Class context,
				ArrayList<String> player_name,
				ArrayList<String> player_picture,
				ArrayList<String> player_status, ArrayList<String> player_fb_id) {
			imageLoader = new ImageLoader(context.getApplicationContext());
			this.context = context;
			this.player_name = player_name;
			this.player_picture = player_picture;
			this.player_status = player_status;
			this.player_Facebook_id = player_fb_id;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			button_remove_visibility = new ArrayList<Integer>();
			if (Game_Status.equalsIgnoreCase("0")) {
				if (Fb_Id.equalsIgnoreCase(Creator_ID)) {
					button_remove_visibility.add(8);
					for (int i = 1; i < player_fb_id.size(); i++) {
						button_remove_visibility.add(0);
					}
				} else {
					for (int i = 0; i < player_fb_id.size(); i++) {
						button_remove_visibility.add(8);
					}
				}
			} else {
				for (int i = 0; i < player_fb_id.size(); i++) {
					button_remove_visibility.add(8);
				}
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return player_name.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return player_name.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			View vi = convertView;
			if (convertView == null) {
				vi = inflater.inflate(R.layout.custom_gamefriend, null);
				holder = new ViewHolder();
				holder.playername = (TextView) vi
						.findViewById(R.id.textView_players);
				holder.playepic = (ImageView) vi.findViewById(R.id.imageView1);
				holder.playerstatus = (TextView) vi
						.findViewById(R.id.textView_status);
				holder.button_remove = (Button) vi
						.findViewById(R.id.button_remove);
				vi.setTag(holder);
			} else {
				holder = (ViewHolder) vi.getTag();
			}
			holder.playername.setId(position);
			holder.playepic.setId(position);
			holder.playerstatus.setId(position);
			holder.button_remove.setId(position);
			holder.playername.setText(player_name.get(position));
			holder.playerstatus.setText(player_status.get(position));

			if (holder.playerstatus.getText().toString()
					.equalsIgnoreCase("Accepted")) {
				holder.playerstatus.setTextColor(Color.parseColor("#145DDE"));
			} else {
				holder.playerstatus.setTextColor(Color.parseColor("#920808"));
			}
			imageLoader.DisplayImage(player_picture.get(position),
					holder.playepic);
			holder.button_remove.setVisibility(button_remove_visibility
					.get(position));
			holder.playername.setTypeface(robot_bold);
			holder.playerstatus.setTypeface(robot_medium);
			holder.button_remove.setTypeface(robot_medium);
			holder.button_remove.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Player_Remove_id = player_Facebook_id.get(position);
					new ProgressTaskLeaveGame(Activity_GameFriends_Class.this)
							.execute();
				}
			});
			return vi;
		}

		public class ViewHolder {
			public TextView playername;
			public TextView playerstatus;
			public ImageView playepic;
			public Button button_remove;

		}
	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_GameFriends_Class activity_login;

		ProgressDialog dialog;

		public ProgressTask(Activity_GameFriends_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_toAPI();
			if (Game_Status.equalsIgnoreCase("0")) {

			} else {
				CHECK_FOR_UPDATE_HUNT_ITEM();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			listView = (ListView) findViewById(R.id.friendsview);

			for (int i = 0; i < player_status.size(); i++) {
				if (player_status.get(i).equalsIgnoreCase("1")) {
					player_status_display.add("Accepted");
				} else if (player_status.get(i).equalsIgnoreCase("0")) {
					player_status_display.add("Waiting");
				} else if (player_status.get(i).equalsIgnoreCase("2")) {
					player_status_display.add("User not available to play.");
				}
			}
			int count = 0;
			for (int i = 0; i < player_status.size(); i++) {
				if (player_status.get(i).equalsIgnoreCase("1")) {
					count++;
				}
			}

			text_number.setText("" + count + " out of " + (player_fb_id.size())
					+ " friends ready to play");
			if (Game_Status.equals("0") && !Creator_ID.equals(Fb_Id)) {
				image_start.setVisibility(8);
			} else {
				image_start.setVisibility(0);
			}
			listView.setAdapter(new MlistAdapter(
					Activity_GameFriends_Class.this, player_name,
					player_picture, player_status_display, player_fb_id));

			dialog.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	private void send_data_toAPI() {
		player_fb_id.clear();
		player_name.clear();
		player_picture.clear();
		player_status.clear();
		player_status_display.clear();
		String url = GAME_START_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		// Creating JSON Parser instance
		JSONParserPostCheck jParser = new JSONParserPostCheck();
		// getting JSON string from URL
		String response = jParser.getJSONFromUrl(url, nameValuePairs);

		JSONObject json;
		try {
			json = new JSONObject(response);
			// Getting Array of Contacts
			result_array = json.getJSONArray(TAG_RESULTS);
			for (int i = 0; i < result_array.length(); i++) {
				JSONObject json2 = result_array.getJSONObject(i);
				Creator_ID = json2.getString(TAG_CREATOR);
				Creator_Name = json2.getString(TAG_CREATOR_NAME);
				player_name.add(Creator_Name);
				player_fb_id.add(Creator_ID);
				player_status.add("1");
				player_picture.add("http://graph.facebook.com/" + Creator_ID
						+ "/picture?width=100&height=100");

				JSONArray game_dat = json2.getJSONArray(TAG_GAMEFRIENDS);
				for (int k = 0; k < game_dat.length(); k++) {
					JSONObject json3 = game_dat.getJSONObject(k);
					player_name.add(json3.getString(TAG_NAME));
					String fb_id = json3.getString(TAG_FB_ID);
					player_fb_id.add(fb_id);
					player_status.add(json3.getString(TAG_STATUS));

					player_picture.add("http://graph.facebook.com/" + fb_id
							+ "/picture?width=100&height=100");

				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
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

	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}

	// Call the above class using this:
	public class ProgressTaskFacebookUpdates extends
			AsyncTask<String, Void, String> {
		Activity_GameFriends_Class activity_login;
		ProgressDialog dialog;

		public ProgressTaskFacebookUpdates(Activity_GameFriends_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			show_toast("Game Started");
			Intent i = new Intent(Activity_GameFriends_Class.this,
					Activity_PlayHome_Class.class);
			i.putExtra("Game_id", GAME_ID);
			i.putExtra("FB_ID", Fb_Id);
			i.putExtra("Fb_Name", Cretor_name);
			startActivity(i);
			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	private void call_API_Notification() {
		// TODO Auto-generated method stub

		String url = NOTIFICATION_MESSAGE_URL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("fb_id", Fb_Id));
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);
		try {
			JSONArray result = json.getJSONArray(TAG_NOTIFICATION);
			for (int i = 0; i < result.length(); i++) {
				// JSONObject json2=result.getJSONObject(i);
				notification_array.add(result.getString(i));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class Progress_Notification extends AsyncTask<String, Void, String> {
		Activity_GameFriends_Class activity;

		public Progress_Notification(
				Activity_GameFriends_Class activity_afterlogin) {
			// TODO Auto-generated constructor stub
			activity = activity_afterlogin;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			call_API_Notification();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			StringBuffer sb = new StringBuffer();

			if (notification_array.size() >= 1) {
				for (int i = 0; i < notification_array.size(); i++) {
					sb.append("*   ");
					sb.append(notification_array.get(i));
					sb.append("\n");

				}
				notification_layout.setVisibility(0);
				text_notification.setText("" + sb.toString());
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

	}

	// Call the above class using this:
	public class ProgressTaskLeaveGame extends AsyncTask<String, Void, String> {

		Activity_GameFriends_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskLeaveGame(Activity_GameFriends_Class context) {
			activity_login = context;
			dialog = new ProgressDialog(activity_login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_to_LeaveGame();
			send_data_toAPI();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			listView = (ListView) findViewById(R.id.friendsview);

			for (int i = 0; i < player_status.size(); i++) {
				if (player_status.get(i).equalsIgnoreCase("1")) {
					player_status_display.add("Accepted");
				} else if (player_status.get(i).equalsIgnoreCase("0")) {
					player_status_display.add("Waiting");
				} else if (player_status.get(i).equalsIgnoreCase("2")) {
					player_status_display.add("User not available to play.");
				}
			}
			int count = 0;
			int uninsatll_user = 0;
			for (int i = 0; i < player_status.size(); i++) {
				if (player_status.get(i).equalsIgnoreCase("1")) {
					count++;
				}
				if (player_status.get(i).equalsIgnoreCase("2")) {
					uninsatll_user++;
				}
			}

			text_number.setText("" + count + " out of " + (player_fb_id.size())
					+ " friends ready to play");
			if (Game_Status.equals("0") && !Creator_ID.equals(Fb_Id)) {
				image_start.setVisibility(8);
			} else {
				image_start.setVisibility(0);
			}
			listView.setAdapter(new MlistAdapter(
					Activity_GameFriends_Class.this, player_name,
					player_picture, player_status_display, player_fb_id));

			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}
	}

	private void send_data_to_LeaveGame() {

		String url = LEAVE_GAME_API.replaceAll(" ", "");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Player_Remove_id));
		// Creating JSON Parser instance
		JSONARRAY jParser = new JSONARRAY();
		// getting JSON string from URL
		JSONArray json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ImageLoader image = new ImageLoader(getApplicationContext());
		image.clearCache();
	}

	private void UPDATE_IMAGE_CHECK() {
		String url = GAME_PICTURE_DETAILS_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_Id));
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		nameValuePairs.add(new BasicNameValuePair("hunt_id", hunt_id));
		// nameValuePairs.add(new BasicNameValuePair("method", "majority"));
		// Creating JSON Parser instance
		JSONParserPostCheck jParser = new JSONParserPostCheck();
		// getting JSON string from URL
		String response = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	private void CHECK_FOR_UPDATE_HUNT_ITEM() {
		Game_Status2 = false;
		hunt_name = null;
		hunt_value = null;
		hunt_id = null;
		fb_id_string = null;
		String url = GAME_DATA_URL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);

		try {
			JSONArray result = json.getJSONArray(TAG_RESULTS);
			// Getting Array of Contacts

			for (int i = 0; i < result.length(); i++) {
				JSONObject json2 = result.getJSONObject(i);
				hunt_name = json2.getString(TAG_HUNT_NAME);
				if (hunt_name.equalsIgnoreCase("complete")) {

				} else {
					hunt_value = json2.getString(TAG_VALUE);
					hunt_id = json2.getString(TAG_HUNT_ID);
					fb_id_string = json2.getString(TAG_FB_ID);
					UPDATE_IMAGE_CHECK();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
