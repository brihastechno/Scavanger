package com.mobile.scavenger;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.shop.JSON.JSONParserPostCheck;

public class BroadCastRecieverService extends Service {

	public static final String BROADCAST_ACTION = "com.scavengerssec.demo";
	private final Handler handler = new Handler();
	Intent intent;
	int counter = 0;
	private static final String TAG_RESULTS = "result";
	private static final String TAG_GAMEDATA = "data";
	private static final String TAG_GAME_STATUS = "status";

	private static final String TAG_GAME_ID = "game_id";
	private static final String TAG_CREATED = "created";
	private static final String TAG_COUNT = "count";
	private static final String TAG_CREATOR_ID = "creatorid";
	private static final String TAG_INVITED_ID = "invitedIds";
	private static final String TAG_ACCEPT_STATUS = "accept_status";

	ArrayList<String> games_name = new ArrayList<String>();
	static ArrayList<String> games_ID = new ArrayList<String>();
	ArrayList<String> palyers = new ArrayList<String>();
	ArrayList<String> dates = new ArrayList<String>();
	static ArrayList<String> status = new ArrayList<String>();
	ArrayList<String> status_diaplay = new ArrayList<String>();
	ArrayList<String> status_accept_aaray = new ArrayList<String>();
	ArrayList<String> cretors_ids = new ArrayList<String>();
	ArrayList<String> fb_ids = new ArrayList<String>();
	String ALL_GAMES_LIST_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/all_games.php";
	String Fb_Id;

	@Override
	public void onCreate() {
		super.onCreate();

		intent = new Intent(BROADCAST_ACTION);

	}

	@Override
	public void onStart(Intent intent, int startId) {

		Fb_Id = intent.getStringExtra("Fb_Id");
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {

			new ProgressTask().execute();

			handler.postDelayed(this, 15000); // 10 seconds
		}
	};

	private void DisplayLoggingInfo() {

		intent.putStringArrayListExtra("players", palyers);
		intent.putStringArrayListExtra("dates", dates);
		intent.putStringArrayListExtra("status", status);
		intent.putStringArrayListExtra("games_ID", games_ID);

		intent.putStringArrayListExtra("cretors_ids", cretors_ids);
		intent.putStringArrayListExtra("status_accept_aaray",
				status_accept_aaray);
		intent.putStringArrayListExtra("fb_ids", fb_ids);

		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
	}

	private void send_data_toAPI() {

		palyers.clear();
		dates.clear();
		status.clear();
		games_ID.clear();

		cretors_ids.clear();
		status_accept_aaray.clear();
		fb_ids.clear();

		String url = ALL_GAMES_LIST_API.replaceAll(" ", "");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_Id));
		// Creating JSON Parser instance
		JSONParserPostCheck jParser = new JSONParserPostCheck();
		// getting JSON string from URL
		String response = jParser.getJSONFromUrl(url, nameValuePairs);
		try {
			// Getting Array of Contacts
			JSONObject json = new JSONObject(response);
			JSONArray result_array = json.getJSONArray(TAG_RESULTS);

			for (int i = 0; i < result_array.length(); i++) {
				JSONObject json2 = result_array.getJSONObject(i);
				JSONArray game_dat = json2.getJSONArray(TAG_GAMEDATA);

				for (int k = 0; k < game_dat.length(); k++) {
					JSONObject json3 = game_dat.getJSONObject(k);

					games_ID.add(json3.getString(TAG_GAME_ID));
					palyers.add(""
							+ (Integer.parseInt(json3.getString(TAG_COUNT)) + 1));

					status.add(json3.getString(TAG_GAME_STATUS));
					dates.add(json3.getString(TAG_CREATED));
					cretors_ids.add(json3.getString(TAG_CREATOR_ID));
					fb_ids.add(json3.getString(TAG_INVITED_ID));
					status_accept_aaray.add(json3.getString(TAG_ACCEPT_STATUS));

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {

		ProgressDialog dialog;

		public ProgressTask() {

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
			DisplayLoggingInfo();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}
	}

}
