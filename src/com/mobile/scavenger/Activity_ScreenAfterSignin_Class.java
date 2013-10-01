package com.mobile.scavenger;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shop.JSON.JSONParserPost;

public class Activity_ScreenAfterSignin_Class extends Activity {
	Button button_Play, button_Share, button_Rules, button_Invite;
	String Fb_Id, Fb_Name;
	SharedPreferences sharedpref;

	private static final String TAG_RESULTS = "result";
	private static final String TAG_NOTIFICATION = "notification";
	ArrayList<String> notification_array = new ArrayList<String>();
	LinearLayout notification_layout;
	TextView text_notification;
	String NOTIFICATION_MESSAGE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/unread_message.php";

	// String NOTIFICATION_MESSAGE_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/unread_message.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenaftersignin_layout);
		notification_array.clear();
		Fb_Id = getIntent().getStringExtra("FB_ID");
		Fb_Name = getIntent().getStringExtra("FB_Name");
		text_notification = (TextView) findViewById(R.id.notifica);
		notification_layout = (LinearLayout) findViewById(R.id.notification_layout);

		if (Fb_Id != null) {
			sharedpref = this.getSharedPreferences("myPrefs",
					MODE_WORLD_READABLE);
			SharedPreferences.Editor prefsEditor = sharedpref.edit();
			prefsEditor.putString("Fb_Id", Fb_Id);
			prefsEditor.putString("FB_Name", Fb_Name);
			prefsEditor.commit();
		} else {
			sharedpref = this.getSharedPreferences("myPrefs",
					MODE_WORLD_READABLE);
			Fb_Id = sharedpref.getString("Fb_Id", null);
			Fb_Name = sharedpref.getString("FB_Name", null);
		}

		button_Play = (Button) findViewById(R.id.button_play);
		button_Share = (Button) findViewById(R.id.button_share);
		button_Rules = (Button) findViewById(R.id.button_rules);
		button_Invite = (Button) findViewById(R.id.button_invite);
		button_Invite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent intent_friend_list = new Intent(
						Activity_ScreenAfterSignin_Class.this,
						Activity_InviteFBFriendList_Class.class);
				intent_friend_list.putExtra("Fb_Id", Fb_Id);
				intent_friend_list.putExtra("Fb_Name", Fb_Name);
				startActivity(intent_friend_list);
			}
		});

		button_Rules.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent intent_rules_class = new Intent(
						Activity_ScreenAfterSignin_Class.this,
						Activity_RulesApp_Class.class);
				startActivity(intent_rules_class);
			}
		});
		button_Share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent intent_share_class = new Intent(
						Activity_ScreenAfterSignin_Class.this,
						Activity_ShareApp_Class.class);
				startActivity(intent_share_class);
			}
		});
		button_Play.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent intent_contplay_class = new Intent(
						Activity_ScreenAfterSignin_Class.this,
						Activity_GameList_Class.class);
				intent_contplay_class.putExtra("FB_ID", Fb_Id);
				intent_contplay_class.putExtra("Fb_Name", Fb_Name);
				startActivity(intent_contplay_class);
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

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// notification_array.clear();
		// new Progress_Notification(Activity_ScreenAfterSignin_Class.this)
		// .execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			finish();
		}
		return super.onKeyDown(keyCode, event);

	}

	public class Progress_Notification extends AsyncTask<String, Void, String> {
		Activity_ScreenAfterSignin_Class activity;
		ProgressDialog dialog;

		public Progress_Notification(
				Activity_ScreenAfterSignin_Class activity_afterlogin) {
			// TODO Auto-generated constructor stub
			activity = activity_afterlogin;
			dialog = new ProgressDialog(activity);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO+ Auto-generated method stub
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

}
