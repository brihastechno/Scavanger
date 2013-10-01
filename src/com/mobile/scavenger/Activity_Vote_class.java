package com.mobile.scavenger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.shop.JSON.JSONParserPost;
import com.shop.JSON.JSONParserPostCheck;

public class Activity_Vote_class extends Activity {
	Button button_pass, button_fail;
	ImageView hunt_item;
	private AsyncFacebookRunner mAsyncRunner;
	private Context mContext;
	public static final String APP_ID = "405265042889545";
	private Facebook mFacebook;
	private SharedPreferences sharedPrefs;
	String Hunt_id, Game_Id, Fb_id, Picture_Url, Picture_Cretor_id, Can_Vote,
			Picture_Thumnail, Pic_Id;
	private static final String TAG_RESULT = "result";
	private static final String TAG_PICTURE_URL = "pic_full_url";
	private static final String TAG_PICTURE_THUMB_URL = "pic_thumb_url";
	private static final String TAG_HUNT_ID = "hunt_id";
	private static final String TAG_PICTURE_CRETOR = "pic_creator_id";
	private static final String TAG_CAN_VOTE = "can_vote";
	private static final String TAG_PICTURE_ID = "pic_id";
	private static final String TAG_NOTIFICATION = "notification";
	JSONArray result_array;
	Bitmap image_bitmap;
	Typeface robot_medium, robot_bold;;
	String vote, message;
	LinearLayout notification_layout;
	TextView text_notification, textView_topmessage;
	ArrayList<String> notification_array = new ArrayList<String>();
	private static final String GAME_PICTURE_DETAILS_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-pic_detail_cal.php";
	private static final String GAME_PIC_VOTING_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-pic_votes_cal.php";
	String NOTIFICATION_MESSAGE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/unread_message.php";
	String Fb_Name, Hunt_name;

	//
	// private static final String GAME_PICTURE_DETAILS_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/game-pic_detail_cal.php";
	// private static final String GAME_PIC_VOTING_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/game-pic_votes_cal.php";
	// String NOTIFICATION_MESSAGE_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/unread_message.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote_layout);
		notification_array.clear();
		text_notification = (TextView) findViewById(R.id.notifica);
		textView_topmessage = (TextView) findViewById(R.id.textView_top);
		notification_layout = (LinearLayout) findViewById(R.id.notification_layout);
		Hunt_id = getIntent().getStringExtra("hunt_id");
		Game_Id = getIntent().getStringExtra("Game_id");
		Fb_Name = getIntent().getStringExtra("Fb_Name");
		Hunt_name = getIntent().getStringExtra("Hunt_name");
		Fb_id = getIntent().getStringExtra("Fb_id");
		button_pass = (Button) findViewById(R.id.button_pass);
		button_fail = (Button) findViewById(R.id.button_fail);
		hunt_item = (ImageView) findViewById(R.id.imageView_huntitems);
		robot_medium = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		robot_bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		textView_topmessage.setTypeface(robot_bold);
		button_pass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean b = check_if_can_vote();
				if (b) {
					vote = "1";
					new ProgressTask_Voting(Activity_Vote_class.this).execute();
					// finish();
				} else {
					show_toast(message);
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
		button_fail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean b = check_if_can_vote();
				if (b) {
					vote = "0";
					new ProgressTask_Voting(Activity_Vote_class.this).execute();
					// finish();
				} else {
					show_toast(message);
				}

			}

		});
		hunt_item.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Picture_Url == null || Picture_Url.equalsIgnoreCase("null")) {
					// show_toast("No Image Found");
				} else {
					Intent i = new Intent(Activity_Vote_class.this,
							Activity_Hunt_Picture.class);
					i.putExtra("image_url", Picture_Url);
					startActivity(i);
				}
			}
		});
		new ProgressTask(Activity_Vote_class.this).execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		notification_array.clear();
		new Progress_Notification(Activity_Vote_class.this).execute();
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_Vote_class activity_login;
		ProgressDialog dialog;

		public ProgressTask(Activity_Vote_class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);
			dialog.setCancelable(false);
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
			Bitmap scales = null;

			if (Picture_Thumnail == null
					|| Picture_Thumnail.equalsIgnoreCase("null")) {
				show_toast("Picture not Uploaded");
				button_fail.setEnabled(false);
				button_pass.setEnabled(false);
			} else {
				if (image_bitmap == null) {
					show_toast("Problem Loading Image");
				} else {
					scales = Bitmap.createScaledBitmap(image_bitmap, 100, 100,
							true);
					hunt_item.setImageBitmap(getRoundedCornerBitmap(scales));
				}
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

	private void send_data_toAPI() {
		String url = GAME_PICTURE_DETAILS_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_id));
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_Id));
		nameValuePairs.add(new BasicNameValuePair("hunt_id", Hunt_id));
		// nameValuePairs.add(new BasicNameValuePair("method", "majority"));
		// Creating JSON Parser instance
		JSONParserPostCheck jParser = new JSONParserPostCheck();
		// getting JSON string from URL
		String response = jParser.getJSONFromUrl(url, nameValuePairs);

		JSONObject json;

		// Getting Array of Contacts
		try {
			json = new JSONObject(response);
			JSONArray result = json.getJSONArray(TAG_RESULT);
			// Getting Array of Contacts

			for (int i = 0; i < result.length(); i++) {
				JSONObject json2 = result.getJSONObject(i);

				Hunt_id = json2.getString(TAG_HUNT_ID);
				Pic_Id = json2.getString(TAG_PICTURE_ID);
				Picture_Url = json2.getString(TAG_PICTURE_URL);
				Picture_Thumnail = json2.getString(TAG_PICTURE_THUMB_URL);
				Picture_Cretor_id = json2.getString(TAG_PICTURE_CRETOR);
				Can_Vote = json2.getString(TAG_CAN_VOTE);

				if (Picture_Thumnail.equalsIgnoreCase("null")) {
					Picture_Url = "null";
				} else {
					InputStream is = new URL(Picture_Thumnail).openStream();
					BitmapFactory.Options options = new BitmapFactory.Options();

					image_bitmap = BitmapFactory
							.decodeStream(is, null, options);

					is.close();
				}
			}

		} catch (JSONException e) {
			Log.e("JsonException", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
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

	private boolean check_if_can_vote() {
		// TODO Auto-generated method stub
		int flag = 0;
		if (Fb_id.equalsIgnoreCase(Picture_Cretor_id)) {
			message = "Sorry you can not vote";
		} else {
			if (Can_Vote.equalsIgnoreCase("yes"))
				flag = 1;
			else
				message = "You Can not Vote";
		}
		if (flag == 1)
			return true;
		else
			return false;

	}

	public class ProgressTask_Voting extends AsyncTask<String, Void, String> {
		Activity_Vote_class activity_login;

		ProgressDialog dialog;

		public ProgressTask_Voting(Activity_Vote_class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_toVoting();
			// setConnection();
			// isSession();
			// {
			// Bundle postParams = new Bundle();
			// if (vote.equalsIgnoreCase("1")) {
			// postParams.putString("message", Fb_Name
			// + " had voted in Favor of your uploaded pic on *"
			// + Hunt_name + "*");
			// } else {
			// postParams.putString("message", Fb_Name
			// + " had voted against your uploaded pic on  *"
			// + Hunt_name + "*");
			// }
			//
			// mAsyncRunner.request(Picture_Cretor_id + "/feed", postParams,
			// "POST", new RequestListener() {
			// @Override
			// public void onMalformedURLException(
			// MalformedURLException e, Object state) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onIOException(IOException e,
			// Object state) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onFileNotFoundException(
			// FileNotFoundException e, Object state) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onFacebookError(FacebookError e,
			// Object state) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onComplete(String response, Object state) {
			// // TODO Auto-generated method stub
			//
			// }
			// }, null);
			//
			// }

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			show_toast("Thanks For Voting");

			dialog.cancel();
			Intent intent_main_class = new Intent(Activity_Vote_class.this,
					Activity_ScreenAfterSignin_Class.class);
			intent_main_class.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent_main_class);
			finish();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	private void send_data_toVoting() {

		String url = GAME_PIC_VOTING_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_id));
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_Id));
		nameValuePairs.add(new BasicNameValuePair("hunt_id", Hunt_id));
		nameValuePairs.add(new BasicNameValuePair("pic_id", Pic_Id));
		nameValuePairs.add(new BasicNameValuePair("vote", vote));

		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	public class Progress_Notification extends AsyncTask<String, Void, String> {
		Activity_Vote_class activity;

		public Progress_Notification(Activity_Vote_class activity_afterlogin) {
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

	private void call_API_Notification() {
		// TODO Auto-generated method stub

		String url = NOTIFICATION_MESSAGE_URL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("fb_id", Fb_id));
		nameValuePairs.add(new BasicNameValuePair("game_id", Game_Id));

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

}
