package com.mobile.scavenger;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

public class Activity_GameList_Class extends Activity {
	ListView listview;
	public static final String APP_ID = "405265042889545";
	ArrayList<String> games_name = new ArrayList<String>();
	static ArrayList<String> games_ID = new ArrayList<String>();
	ArrayList<String> palyers = new ArrayList<String>();
	ArrayList<String> dates = new ArrayList<String>();
	static ArrayList<String> status = new ArrayList<String>();
	ArrayList<String> status_diaplay = new ArrayList<String>();
	ArrayList<String> status_accept_aaray = new ArrayList<String>();
	ArrayList<String> cretors_ids = new ArrayList<String>();
	ArrayList<String> fb_ids = new ArrayList<String>();
	static String Fb_Id, Cretor_name;
	private static final String TAG_RESULTS = "result";
	private static final String TAG_GAMEDATA = "data";
	private static final String TAG_GAME_STATUS = "status";
	private static final String TAG_MESSGAE = "message";
	private static final String TAG_GAME_ID = "game_id";
	private static final String TAG_CREATED = "created";
	private static final String TAG_COUNT = "count";
	private static final String TAG_CREATOR_ID = "creatorid";
	private static final String TAG_INVITED_ID = "invitedIds";
	private static final String TAG_ACCEPT_STATUS = "accept_status";
	private static final String TAG_NOTIFICATION = "notification";
	JSONArray result_array = null, data_array = null;
	static Typeface robot_medium;
	static Typeface robot_bold;
	String Game_ID_Delete, Game_ID_Leave, Game_ID_Restart, Game_ID_REMINDER;
	String Invited_FB_id;
	private AsyncFacebookRunner mAsyncRunner;
	private Facebook mFacebook;
	private Context mContext;
	private SharedPreferences sharedPrefs;
	String CREATOR_OF_GAME;

	LinearLayout custom_list;
	String[] arra;
	String GAME_ID, game_Status;
	ArrayList<String> notification_array = new ArrayList<String>();
	LinearLayout notification_layout;
	TextView text_notification;
	TextView text_no_Game;
	String NOTIFICATION_MESSAGE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/unread_message.php";
	String ALL_GAMES_LIST_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/all_games.php";
	String DELETE_GAME_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/delete-game.php";
	String LEAVE_GAME_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-leave_cal.php";
	String GAME_INVITATION_ACCEPT_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/inv-response_cal.php";
	// String GAME_RESTART_API =
	// "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-restart.php";
	String GAME_RESTART_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/restart-game.php";
	String GAME_REMINDER_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game_reminder.php";
	// String NOTIFICATION_MESSAGE_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/unread_message.php";
	// String ALL_GAMES_LIST_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/all_games.php";
	// String DELETE_GAME_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/delete-game.php";
	// String LEAVE_GAME_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/game-leave_cal.php";
	// String GAME_INVITATION_ACCEPT_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/inv-response_cal.php";
	Intent intent;
	ProgressDialog main_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gamelist_layoout);

		text_notification = (TextView) findViewById(R.id.notifica);
		notification_layout = (LinearLayout) findViewById(R.id.notification_layout);
		text_no_Game = (TextView) findViewById(R.id.textView_noimage);
		notification_array.clear();
		Fb_Id = getIntent().getStringExtra("FB_ID");
		Cretor_name = getIntent().getStringExtra("Fb_Name");
		games_name.clear();
		palyers.clear();
		dates.clear();
		status.clear();
		games_ID.clear();
		status_diaplay.clear();
		cretors_ids.clear();
		fb_ids.clear();
		status_accept_aaray.clear();
		// custom_list=(LinearLayout)findViewById(R.id.lay1);

		listview = (ListView) findViewById(R.id.listView1);
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
		main_dialog = new ProgressDialog(this);
		// new ProgressTask(Activity_GameList_Class.this).execute();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		main_dialog.show();

		// notification_array.clear();
		// new Progress_Notification(Activity_GameList_Class.this).execute();
		// // new ProgressTask(Activity_GameList_Class.this).execute();
		intent = new Intent(this, BroadCastRecieverService.class);
		intent.putExtra("Fb_Id", Fb_Id);

		startService(intent);
		registerReceiver(broadcastReceiver, new IntentFilter(
				BroadCastRecieverService.BROADCAST_ACTION));
		notification_array.clear();
		new Progress_Notification(Activity_GameList_Class.this).execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent);
	}

	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
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

	public class MlistAdapter extends BaseAdapter {
		ArrayList<String> games_name;
		ArrayList<String> palyers;
		ArrayList<String> dates;
		ArrayList<String> status_display;
		Activity_GameList_Class context;
		ArrayList<String> fb_id;
		ArrayList<Integer> sparse;
		ArrayList<Integer> sparse2;
		ArrayList<Integer> sparse3;
		ArrayList<String> creators;
		private LayoutInflater inflater = null;

		public MlistAdapter(Activity_GameList_Class context,
				ArrayList<String> games_name, ArrayList<String> players,
				ArrayList<String> date, ArrayList<String> status_sec,
				ArrayList<String> fb_ids, ArrayList<String> creator_array) {
			this.context = context;
			robot_bold = Typeface.createFromAsset(context.getAssets(),
					"roboto_bold.ttf");
			robot_medium = Typeface.createFromAsset(context.getAssets(),
					"robotomedium.ttf");
			this.games_name = games_name;
			this.palyers = players;
			this.dates = date;
			creators = creator_array;
			this.status_display = status_sec;
			fb_id = fb_ids;
			sparse = new ArrayList<Integer>();
			sparse2 = new ArrayList<Integer>();
			sparse3 = new ArrayList<Integer>();
			sparse.clear();
			sparse2.clear();
			sparse3.clear();
			for (int i = 0; i < status.size(); i++) {
				sparse3.add(8);

				boolean b = creators.get(i).equalsIgnoreCase(Fb_Id);
				if (status.get(i).equals("1") || b == false) {
					sparse2.add(8);
					sparse.add(0);
				} else {
					sparse2.add(0);
					sparse.add(8);
				}
				if (status.get(i).equals("1")
						|| status_accept_aaray.get(i).equalsIgnoreCase("1")) {
					sparse.set(i, 8);
				}
				if (status.get(i).equals("3")) {
					if (creators.get(i).equalsIgnoreCase(Fb_Id)) {
						sparse2.set(i, 8);
						sparse.set(i, 8);
						sparse3.set(i, 0);
					} else {
						sparse2.set(i, 8);
						sparse.set(i, 8);
						sparse3.set(i, 8);
					}
				}

			}

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return games_name.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return games_name.get(position);
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
				vi = inflater.inflate(R.layout.custom_gamerow_layout, null);
				holder = new ViewHolder();
				holder.gamename = (TextView) vi
						.findViewById(R.id.textView_gamename);
				holder.imagestart = (ImageView) vi
						.findViewById(R.id.imageView_start);
				holder.number_players = (TextView) vi
						.findViewById(R.id.textView_players);
				holder.status_game = (TextView) vi
						.findViewById(R.id.textView_active);

				holder.text_act = (TextView) vi.findViewById(R.id.textView_act);
				holder.text_count = (TextView) vi
						.findViewById(R.id.textView_pl);
				holder.text_dat = (TextView) vi.findViewById(R.id.textView_dat);

				holder.date = (TextView) vi.findViewById(R.id.textView_date);
				holder.button_delete = (Button) vi
						.findViewById(R.id.button_delete);
				holder.button_invite = (Button) vi
						.findViewById(R.id.button_invite);
				holder.button_accept = (Button) vi
						.findViewById(R.id.button_accept);
				holder.button_leave = (Button) vi
						.findViewById(R.id.button_leave);
				holder.button_delete_game = (Button) vi
						.findViewById(R.id.button_delete_game);
				holder.button_restart = (Button) vi
						.findViewById(R.id.button_restart);
				vi.setTag(holder);
			} else {
				holder = (ViewHolder) vi.getTag();
			}

			holder.gamename.setId(position);
			holder.imagestart.setId(position);
			holder.number_players.setId(position);
			holder.status_game.setId(position);
			holder.date.setId(position);
			holder.button_delete.setId(position);
			holder.button_invite.setId(position);
			holder.button_leave.setId(position);
			holder.button_accept.setId(position);
			holder.button_delete_game.setId(position);
			holder.button_restart.setId(position);

			holder.button_delete.setVisibility(sparse2.get(position));
			holder.button_invite.setVisibility(sparse2.get(position));
			holder.button_delete_game.setVisibility(sparse3.get(position));
			holder.button_restart.setVisibility(sparse3.get(position));
			holder.gamename.setText(games_name.get(position));
			holder.date.setText(dates.get(position));
			holder.number_players.setText(palyers.get(position));
			holder.status_game.setText(status_display.get(position));
			if (holder.status_game.getText().toString()
					.equalsIgnoreCase("Active")) {
				holder.status_game.setTextColor(Color.parseColor("#145DDE"));
			} else {
				holder.status_game.setTextColor(Color.parseColor("#920808"));
			}
			holder.button_accept.setVisibility(sparse.get(position));
			holder.button_leave.setVisibility(sparse.get(position));
			holder.button_delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notification_layout.setVisibility(8);

					Game_ID_Delete = games_ID.get(position);
					new ProgressTaskDeleteGame(Activity_GameList_Class.this)
							.execute();
				}
			});
			holder.button_leave.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notification_layout.setVisibility(8);
					Game_ID_Leave = games_ID.get(position);
					new ProgressTaskLeaveGame(Activity_GameList_Class.this)
							.execute();
				}
			});
			holder.button_accept.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notification_layout.setVisibility(8);
					GAME_ID = games_ID.get(position);
					game_Status = "1";
					CREATOR_OF_GAME = creators.get(position);

					new ProgressTaskAccept(Activity_GameList_Class.this)
							.execute();

				}
			});

			holder.button_invite.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notification_layout.setVisibility(8);

					Game_ID_REMINDER = games_ID.get(position);

					arra = fb_id.get(position).split(",");
					if (arra.length >= 1) {
						if (arra[0].length() > 0) {

							new ProgressTaskReminder(
									Activity_GameList_Class.this).execute();
						} else {
							show_toast("Reminder Can not send");
						}

					} else {
						show_toast("Reminder Can not send");
					}

				}
			});
			holder.button_restart
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							notification_layout.setVisibility(8);
							Game_ID_Restart = games_ID.get(position);
							new ProgressTaskRestartGame(
									Activity_GameList_Class.this).execute();
						}
					});
			vi.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notification_layout.setVisibility(8);

					Intent i = new Intent(context,
							Activity_GameFriends_Class.class);
					i.putExtra("game_id", games_ID.get(position));
					i.putExtra("FB_ID", Fb_Id);
					i.putExtra("Fb_Name", Cretor_name);
					i.putExtra("Status_game", status.get(position));

					context.startActivity(i);

				}
			});
			holder.button_delete_game
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							notification_layout.setVisibility(8);

							Game_ID_Delete = games_ID.get(position);
							new ProgressTaskDeleteGame(
									Activity_GameList_Class.this).execute();
						}
					});
			holder.date.setTypeface(robot_bold);
			holder.button_invite.setTypeface(robot_medium);
			holder.button_delete.setTypeface(robot_medium);
			holder.gamename.setTypeface(robot_bold);
			holder.number_players.setTypeface(robot_bold);
			holder.text_act.setTypeface(robot_medium);
			holder.text_dat.setTypeface(robot_medium);
			holder.text_count.setTypeface(robot_medium);
			holder.button_leave.setTypeface(robot_medium);
			holder.button_accept.setTypeface(robot_medium);
			holder.button_delete_game.setTypeface(robot_medium);
			holder.button_restart.setTypeface(robot_medium);
			return vi;
		}

		public class ViewHolder {
			public TextView gamename;
			public TextView number_players, text_count;
			public TextView date, text_dat;
			public ImageView imagestart;
			public TextView status_game, text_act;
			public Button button_delete, button_invite, button_leave,
					button_accept, button_restart, button_delete_game;

		}
	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_GameList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTask(Activity_GameList_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(activity_login);

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
			int k = 0;
			for (int i = 0; i < games_ID.size(); i++) {

				games_name.add("Game " + (i + 1));
				if (status.get(i).equalsIgnoreCase("0")) {
					status_diaplay.add("Waiting");
				} else if (status.get(i).equalsIgnoreCase("1")) {
					status_diaplay.add("Active");
					k++;
				} else if (status.get(i).equalsIgnoreCase("2")) {
					status_diaplay.add("Pause");
				} else if (status.get(i).equalsIgnoreCase("3")) {
					status_diaplay.add("Finished");
				}

			}

			listview.setAdapter(new MlistAdapter(Activity_GameList_Class.this,
					games_name, palyers, dates, status_diaplay, fb_ids,
					cretors_ids));
			text_no_Game.setVisibility(0);

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
		games_name.clear();
		palyers.clear();
		dates.clear();
		status.clear();
		games_ID.clear();
		status_diaplay.clear();
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

		JSONObject json;

		try {

			json = new JSONObject(response);
			// Getting Array of Contacts
			result_array = json.getJSONArray(TAG_RESULTS);

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

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// Call the above class using this:
	public class ProgressTaskDeleteGame extends AsyncTask<String, Void, String> {

		Activity_GameList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskDeleteGame(Activity_GameList_Class context) {
			activity_login = context;
			dialog = new ProgressDialog(activity_login);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_to_DeleteGame();
			send_data_toAPI();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			int k = 0;
			for (int i = 0; i < games_ID.size(); i++) {

				games_name.add("Game " + (i + 1));
				if (status.get(i).equalsIgnoreCase("0")) {
					status_diaplay.add("Waiting");
				} else if (status.get(i).equalsIgnoreCase("1")) {
					status_diaplay.add("Active");
					k++;
				} else if (status.get(i).equalsIgnoreCase("2")) {
					status_diaplay.add("Pause");

				} else if (status.get(i).equalsIgnoreCase("3")) {
					status_diaplay.add("Finished");
				}

			}
			MlistAdapter mlistAdapter = new MlistAdapter(
					Activity_GameList_Class.this, games_name, palyers, dates,
					status_diaplay, fb_ids, cretors_ids);
			mlistAdapter.notifyDataSetChanged();
			listview.setAdapter(mlistAdapter);

			show_toast("Game Deleted");
			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}
	}

	// Call the above class using this:
	public class ProgressTaskLeaveGame extends AsyncTask<String, Void, String> {

		Activity_GameList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskLeaveGame(Activity_GameList_Class context) {
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
			int k = 0;
			for (int i = 0; i < games_ID.size(); i++) {

				games_name.add("Game " + (i + 1));
				if (status.get(i).equalsIgnoreCase("0")) {
					status_diaplay.add("Waiting");
				} else if (status.get(i).equalsIgnoreCase("1")) {
					status_diaplay.add("Active");
					k++;
				} else if (status.get(i).equalsIgnoreCase("2")) {
					status_diaplay.add("Pause");
				} else if (status.get(i).equalsIgnoreCase("3")) {
					status_diaplay.add("Finished");
				}

			}

			MlistAdapter mlistAdapter = new MlistAdapter(
					Activity_GameList_Class.this, games_name, palyers, dates,
					status_diaplay, fb_ids, cretors_ids);
			mlistAdapter.notifyDataSetChanged();
			listview.setAdapter(mlistAdapter);
			show_toast("Game Removed By Your List");

			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}
	}

	private void send_data_to_DeleteGame() {

		String url = DELETE_GAME_API.replaceAll(" ", "");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_ID_Delete));
		// Creating JSON Parser instance
		JSONARRAY jParser = new JSONARRAY();
		// getting JSON string from URL
		JSONArray json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	private void send_data_to_LeaveGame() {

		String url = LEAVE_GAME_API.replaceAll(" ", "");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_ID_Leave));
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_Id));
		// Creating JSON Parser instance
		JSONARRAY jParser = new JSONARRAY();
		// getting JSON string from URL
		JSONArray json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	// Call the above class using this:
	public class ProgressTaskReminder extends AsyncTask<String, Void, String> {
		Activity_GameList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskReminder(Activity_GameList_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Game_Reminder_api();
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			show_toast("Reminder Sent");
			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	private void invitation_accept() {

		String url = GAME_INVITATION_ACCEPT_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("fb_id", Fb_Id));
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		nameValuePairs.add(new BasicNameValuePair("status", game_Status));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	// Call the above class using this:
	public class ProgressTaskAccept extends AsyncTask<String, Void, String> {
		Activity_GameList_Class activity_login;
		Dialog dialog;

		public ProgressTaskAccept(Activity_GameList_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			invitation_accept();

			send_data_toAPI();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			int k = 0;
			for (int i = 0; i < games_ID.size(); i++) {
				games_name.add("Game " + (i + 1));
				if (status.get(i).equalsIgnoreCase("0")) {
					status_diaplay.add("Waiting");
				} else if (status.get(i).equalsIgnoreCase("1")) {
					status_diaplay.add("Active");
					k++;
				} else if (status.get(i).equalsIgnoreCase("2")) {
					status_diaplay.add("Pause");
				} else if (status.get(i).equalsIgnoreCase("3")) {
					status_diaplay.add("Finished");
				}
			}

			MlistAdapter mlistAdapter = new MlistAdapter(
					Activity_GameList_Class.this, games_name, palyers, dates,
					status_diaplay, fb_ids, cretors_ids);
			mlistAdapter.notifyDataSetChanged();
			listview.setAdapter(mlistAdapter);
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
		nameValuePairs.add(new BasicNameValuePair("game_id", "0"));

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
		Activity_GameList_Class activity;

		public Progress_Notification(Activity_GameList_Class activity_afterlogin) {
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
			main_dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

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

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {

		palyers.clear();
		dates.clear();
		status.clear();
		games_ID.clear();
		cretors_ids.clear();
		cretors_ids.clear();
		fb_ids.clear();
		status_diaplay.clear();
		games_name.clear();
		status_accept_aaray.clear();
		palyers = intent.getStringArrayListExtra("players");
		dates = intent.getStringArrayListExtra("dates");
		status = intent.getStringArrayListExtra("status");
		games_ID = intent.getStringArrayListExtra("games_ID");
		cretors_ids = intent.getStringArrayListExtra("cretors_ids");
		fb_ids = intent.getStringArrayListExtra("fb_ids");
		status_accept_aaray = intent
				.getStringArrayListExtra("status_accept_aaray");

		int k = 0;
		for (int i = 0; i < games_ID.size(); i++) {

			games_name.add("Game " + (i + 1));
			if (status.get(i).equalsIgnoreCase("0")) {
				status_diaplay.add("Waiting");
			} else if (status.get(i).equalsIgnoreCase("1")) {
				status_diaplay.add("Active");
				k++;
			} else if (status.get(i).equalsIgnoreCase("2")) {
				status_diaplay.add("Pause");
			} else if (status.get(i).equalsIgnoreCase("3")) {
				status_diaplay.add("Finished");
			}

		}

		MlistAdapter mlistadapter = new MlistAdapter(
				Activity_GameList_Class.this, games_name, palyers, dates,
				status_diaplay, fb_ids, cretors_ids);

		listview.setAdapter(mlistadapter);

		text_no_Game.setVisibility(0);

	}

	// Call the above class using this:
	public class ProgressTaskRestartGame extends
			AsyncTask<String, Void, String> {

		Activity_GameList_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskRestartGame(Activity_GameList_Class context) {
			activity_login = context;
			dialog = new ProgressDialog(activity_login);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_to_RestartGame();

			send_data_toAPI();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			int k = 0;
			for (int i = 0; i < games_ID.size(); i++) {

				games_name.add("Game " + (i + 1));
				if (status.get(i).equalsIgnoreCase("0")) {
					status_diaplay.add("Waiting");
				} else if (status.get(i).equalsIgnoreCase("1")) {
					status_diaplay.add("Active");
					k++;
				} else if (status.get(i).equalsIgnoreCase("2")) {
					status_diaplay.add("Pause");
				} else if (status.get(i).equalsIgnoreCase("3")) {
					status_diaplay.add("Finished");
				}

			}

			MlistAdapter mlistAdapter = new MlistAdapter(
					Activity_GameList_Class.this, games_name, palyers, dates,
					status_diaplay, fb_ids, cretors_ids);
			mlistAdapter.notifyDataSetChanged();
			listview.setAdapter(mlistAdapter);
			show_toast("Game Restart ");

			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}
	}

	private void send_data_to_RestartGame() {

		String url = GAME_RESTART_API.replaceAll(" ", "");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_ID_Restart));
		// nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_Id));
		// Creating JSON Parser instance
		JSONARRAY jParser = new JSONARRAY();
		// getting JSON string from URL
		JSONArray json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	private void Game_Reminder_api() {

		String url = GAME_REMINDER_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("fb_id", Fb_Id));
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_ID_REMINDER));

		// nameValuePairs.add(new BasicNameValuePair("status", game_Status));
		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

}
