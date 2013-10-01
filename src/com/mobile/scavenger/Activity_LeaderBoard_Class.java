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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shop.JSON.JSONParserPost;

public class Activity_LeaderBoard_Class extends Activity {

	Button button_back;
	ListView listView;
	private final ArrayList<String> arraylist_name = new ArrayList<String>();
	private final ArrayList<String> arraylist_url = new ArrayList<String>();
	private final ArrayList<String> arraylist_points = new ArrayList<String>();
	String Game_ID;
	String LEADBOARD_LIST_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/user_points.php";
	String LEADERBOARD_LIST_FULL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/all_user_points.php";
	//
	// String LEADBOARD_LIST_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/user_points.php";
	// String LEADERBOARD_LIST_FULL =
	// "http://brstdev.com/scavanger/gcm_send_notification/all_user_points";
	private static final String TAG_RESULTS = "result";
	private static final String TAG_NAME = "name";
	private static final String TAG_POINTS = "points";
	private static final String TAG_FB_ID = "fb_id";
	private static final String TAG_HUNT_ID = "hunt_id";
	String hunt_id, current_hunt;
	TextView text_round;
	Typeface robot_bold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard_layout);
		hunt_id = getIntent().getStringExtra("hunt_id");
		Game_ID = getIntent().getStringExtra("game_id");
		listView = (ListView) findViewById(R.id.leaderboard_view);
		text_round = (TextView) findViewById(R.id.textView_round);

		robot_bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		text_round.setTypeface(robot_bold);
		listView.setCacheColorHint(Color.TRANSPARENT);
		button_back = (Button) findViewById(R.id.button_back);
		button_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		new ProgressTask(Activity_LeaderBoard_Class.this).execute();
	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_LeaderBoard_Class activity_login;

		ProgressDialog dialog;

		public ProgressTask(Activity_LeaderBoard_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_toAPI();
			// send_data_toAPI2();
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// Only the original owner thread can touch its views

			if (current_hunt.equalsIgnoreCase("10")) {
				if (hunt_id.length() >= 1) {
					text_round.setText("THIS IS ROUND " + current_hunt
							+ " OF 10");
				} else {
					text_round.setText("THIS GAME HAS BEEN COMPLETED");
				}
			} else {
				if (current_hunt.equalsIgnoreCase("0")) {
					text_round.setText("THIS GAME HAS BEEN COMPLETED");
				} else {
					text_round.setText("THIS IS ROUND " + current_hunt
							+ " OF 10");
				}
			}
			listView.setAdapter(new MlistAdapater(
					Activity_LeaderBoard_Class.this, arraylist_name,
					arraylist_points, arraylist_url));

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

		String url = LEADBOARD_LIST_URL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_ID));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);
		try {
			JSONArray jsonarray = json.getJSONArray(TAG_RESULTS);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject json2 = jsonarray.getJSONObject(i);
				String id = json2.getString(TAG_FB_ID);
				arraylist_name.add(json2.getString(TAG_NAME));
				arraylist_url.add("http://graph.facebook.com/" + id
						+ "/picture?width=100&height=100");
				arraylist_points.add(json2.getString(TAG_POINTS));
				current_hunt = json2.getString(TAG_HUNT_ID);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void send_data_toAPI2() {

		arraylist_name.clear();
		arraylist_points.clear();
		arraylist_url.clear();
		String url = LEADERBOARD_LIST_FULL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("game_id", Game_ID));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);
		try {
			JSONArray jsonarray = json.getJSONArray(TAG_RESULTS);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject json2 = jsonarray.getJSONObject(i);
				String id = json2.getString(TAG_FB_ID);
				arraylist_name.add(json2.getString(TAG_NAME));
				arraylist_url.add("http://graph.facebook.com/" + id
						+ "/picture?width=100&height=100");
				arraylist_points.add(json2.getString(TAG_POINTS));
				current_hunt = json2.getString(TAG_HUNT_ID);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static class MlistAdapater extends BaseAdapter {
		private Activity_LeaderBoard_Class activity;
		private ArrayList<String> person_name;
		private ArrayList<String> person_Pic;
		private ArrayList<String> person_Points;
		private LayoutInflater inflater = null;
		public ImageLoader imageLoader;
		boolean[] checkBoxState;

		int counter;
		Typeface robot_medium, robot_bold;
		ArrayList<Integer> image_background;

		public MlistAdapater(
				Activity_LeaderBoard_Class activity_LeaderBoard_Class,
				ArrayList<String> arraylist_name,
				ArrayList<String> arraylist_points,
				ArrayList<String> arraylist_url) {

			activity = activity_LeaderBoard_Class;
			person_name = arraylist_name;
			person_Pic = arraylist_url;
			person_Points = arraylist_points;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(activity.getApplicationContext());

			robot_medium = Typeface.createFromAsset(activity.getAssets(),
					"robotomedium.ttf");
			robot_bold = Typeface.createFromAsset(activity.getAssets(),
					"roboto_bold.ttf");
		}

		public int getCount() {
			return person_name.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			View vi = convertView;
			if (convertView == null) {
				vi = inflater.inflate(R.layout.custom_leaderboard_row, null);
				holder = new ViewHolder();
				holder.name = (TextView) vi.findViewById(R.id.rowtext_top);
				holder.image = (ImageView) vi.findViewById(R.id.imageView1);
				holder.points = (TextView) vi.findViewById(R.id.text_points);

				vi.setTag(holder);
			} else {
				holder = (ViewHolder) vi.getTag();
			}
			holder.name.setId(position);
			holder.points.setId(position);
			holder.image.setId(position);
			holder.name.setTypeface(robot_bold);
			holder.points.setTypeface(robot_medium);
			holder.name.setText(person_name.get(position));
			holder.points.setText(person_Points.get(position));

			imageLoader.DisplayImage(person_Pic.get(position), holder.image);

			return vi;
		}

		public static class ViewHolder {
			public TextView name, points;

			public ImageView image;

		}
	}
}
