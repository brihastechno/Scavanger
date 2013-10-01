package com.mobile.scavenger;

import static com.androidhive.pushnotification.CommonUtilities.EXTRA_MESSAGE;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhive.pushnotification.WakeLocker;
import com.google.android.gcm.GCMRegistrar;
import com.shop.JSON.JSONParserPost;

public class Activity_Notification_Class extends Activity {
	TextView text_message;
	Button button_yes, button_no;
	String status;
	String GAME_ID, Fb_Id;
	ListView notification_listview;
	String NOTIFICATION_MESSAGE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/unread_message.php";
	ArrayList<String> notification_array = new ArrayList<String>();
	private static final String TAG_NOTIFICATION = "notification";
	String Message;
	Typeface robot_bold, robot_medium;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_layout);
		Fb_Id = getIntent().getStringExtra("fb_id");
		Message = getIntent().getStringExtra("message");
		robot_bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		robot_medium = Typeface
				.createFromAsset(getAssets(), "robotomedium.ttf");
		notification_array.clear();
		notification_listview = (ListView) findViewById(R.id.listView1);
		new Progress_Notification(Activity_Notification_Class.this).execute();
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
			text_message.setText(newMessage);
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();
			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(getApplicationContext());
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	public class Progress_Notification extends AsyncTask<String, Void, String> {
		Activity_Notification_Class activity;
		ProgressDialog dialog;

		public Progress_Notification(
				Activity_Notification_Class activity_afterlogin) {
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
				MlistAdapter mlistadapter = new MlistAdapter(
						Activity_Notification_Class.this, notification_array);
				notification_listview.setAdapter(mlistadapter);

			} else {
				notification_array.add(Message);
				MlistAdapter mlistadapter = new MlistAdapter(
						Activity_Notification_Class.this, notification_array);
				notification_listview.setAdapter(mlistadapter);

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

	public class MlistAdapter extends BaseAdapter {
		ArrayList<String> notification_arraylist;
		Activity_Notification_Class context;
		private LayoutInflater inflater = null;

		public MlistAdapter(Activity_Notification_Class context,
				ArrayList<String> nottification_array) {
			this.context = context;
			this.notification_arraylist = nottification_array;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return notification_arraylist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return notification_arraylist.get(position);
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
				vi = inflater.inflate(R.layout.notification_list_row, null);
				holder = new ViewHolder();
				holder.notification_text = (TextView) vi
						.findViewById(R.id.textView_row);
				vi.setTag(holder);
			} else {
				holder = (ViewHolder) vi.getTag();
			}

			holder.notification_text.setId(position);

			holder.notification_text.setText(notification_arraylist
					.get(position));
			holder.notification_text.setTypeface(robot_medium);
			vi.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(Activity_Notification_Class.this,
							Activity_MainActivity_Class.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					finish();
				}
			});

			return vi;
		}

		public class ViewHolder {
			public TextView notification_text;

		}
	}

}
