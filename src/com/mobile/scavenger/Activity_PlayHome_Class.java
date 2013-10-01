package com.mobile.scavenger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.shop.JSON.JSONParserPost;

public class Activity_PlayHome_Class extends Activity {
	Button button_leaderboard, button_vote, button_upload, button_home;
	String GAME_ID, Fb_Id;
	private static final String TAG_HUNT_ID = "id";
	private static final String TAG_HUNT_NAME = "hunt_name";
	private static final String TAG_VALUE = "hunt_value";
	private static final String TAG_GAME_ID = "game_id";
	private static final String TAG_FB_ID = "fb_id";
	private static final String TAG_RESULTS = "result";
	private static final String TAG_NOTIFICATION = "notification";
	private static final String TAG_IMAGE_UPLOAD = "image_upload";

	private static final String TAG_GAME_STATUS = "image_upload";
	public static final String APP_ID = "405265042889545";

	JSONArray result_array;
	String hunt_name, hunt_id, hunt_value, image_upload_check;
	TextView hunt_items;
	String image_string;
	ImageView advertise;
	private Context mContext;
	boolean hunt_s;
	static Typeface robot_bold, roboto_medium;
	private static final int PICK_FROM_FILE = 3;

	private static final int TAKE_PICTURE = 4;
	private Uri mImageCaptureUri;
	AlertDialog.Builder builder;
	private AsyncFacebookRunner mAsyncRunner;
	private Facebook mFacebook;
	private SharedPreferences sharedPrefs;
	ArrayList<String> fb_id_list = new ArrayList<String>();
	String Cretor_name;
	Bitmap bigmap;
	String fb_id_string;
	LinearLayout notification_layout;
	TextView text_notification;
	ArrayList<String> notification_array = new ArrayList<String>();
	String GAME_DATA_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-table.php";
	String UPLOAD_PICTURE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-upload-pic_cal.php";
	String NOTIFICATION_MESSAGE_URL = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/unread_message.php";
	String CAN_UPLOAD_IMAGE_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/can_upload_status.php";
	// String GAME_DATA_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/game-table.php";
	// String UPLOAD_PICTURE_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/game-upload-pic_cal.php";
	// String NOTIFICATION_MESSAGE_URL =
	// "http://brstdev.com/scavanger/gcm_send_notification/unread_message.php";
	boolean Game_Status;

	final public static CharSequence[] methods = { "Select using Gallery" };
	Uri targeturi;
	int image_orientation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_playhome_layout);
		text_notification = (TextView) findViewById(R.id.notifica);
		notification_layout = (LinearLayout) findViewById(R.id.notification_layout);
		fb_id_list.clear();
		Cretor_name = getIntent().getStringExtra("Fb_Name");
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Image: ");

		GAME_ID = getIntent().getStringExtra("Game_id");
		Fb_Id = getIntent().getStringExtra("FB_ID");
		hunt_s = getIntent().getBooleanExtra("Hunt", false);
		robot_bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		roboto_medium = Typeface.createFromAsset(getAssets(),
				"robotomedium.ttf");

		button_leaderboard = (Button) findViewById(R.id.button_leaderboard);
		button_vote = (Button) findViewById(R.id.button_vote);
		button_upload = (Button) findViewById(R.id.button_upload);
		button_home = (Button) findViewById(R.id.button_home);
		hunt_items = (TextView) findViewById(R.id.text_hunt);

		button_home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent intent_main_class = new Intent(
						Activity_PlayHome_Class.this,
						Activity_ScreenAfterSignin_Class.class);
				intent_main_class.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent_main_class);
				finish();
			}
		});
		button_leaderboard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent i = new Intent(Activity_PlayHome_Class.this,
						Activity_LeaderBoard_Class.class);
				i.putExtra("game_id", GAME_ID);
				if (hunt_name.equalsIgnoreCase("complete")) {
					i.putExtra("hunt_id", "");
				} else {
					i.putExtra("hunt_id", hunt_id);
				}
				startActivity(i);
			}
		});
		button_upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				button_upload.setEnabled(false);
				new Progress_Upload_Image_Check(Activity_PlayHome_Class.this)
						.execute();
			}
		});
		button_vote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notification_layout.setVisibility(8);
				Intent i = new Intent(Activity_PlayHome_Class.this,
						Activity_Vote_class.class);
				i.putExtra("Fb_id", Fb_Id);
				i.putExtra("Game_id", GAME_ID);
				i.putExtra("hunt_id", hunt_id);
				i.putExtra("Fb_Name", Cretor_name);
				i.putExtra("Hunt_name", hunt_items.getText().toString());
				startActivity(i);
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
		hunt_items.setTypeface(robot_bold);
		builder.setItems(methods, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				switch (which) {
				case 0:
					image_orientation = 0;
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, PICK_FROM_FILE);

					break;
				case 1:

					Intent getCameraImage = new Intent(
							"android.media.action.IMAGE_CAPTURE");

					File cameraFolder = null;

					if (android.os.Environment.getExternalStorageState()
							.equals(android.os.Environment.MEDIA_MOUNTED))
						cameraFolder = new File(android.os.Environment
								.getExternalStorageDirectory(),
								"MobileScavenger");

					if (!cameraFolder.exists())
						cameraFolder.mkdirs();

					File photo = new File(Environment
							.getExternalStorageDirectory(),
							"MobileScavenger/Hunt_"
									+ System.currentTimeMillis() + ".jpg");
					getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photo));
					targeturi = Uri.fromFile(photo);

					startActivityForResult(getCameraImage, TAKE_PICTURE);
					break;

				default:
					break;
				}
			}
		});
		hunt_items.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean boolean_check_url = URLUtil.isValidUrl(hunt_items
						.getText().toString());
				if (boolean_check_url) {

					Intent intent_hunt_url = new Intent(
							Activity_PlayHome_Class.this,
							Activity_Open_Photos.class);
					intent_hunt_url.putExtra("hunt_url", ""
							+ hunt_items.getText().toString());
					startActivity(intent_hunt_url);
				}

			}
		});
		// new ProgressTask(Activity_PlayHome_Class.this).execute();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();

		notification_array.clear();
		// new Progress_Notification(Activity_PlayHome_Class.this).execute();
		new ProgressTask(Activity_PlayHome_Class.this).execute();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		return;
	}

	// Call the above class using this:
	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_PlayHome_Class activity_login;

		ProgressDialog dialog;

		public ProgressTask(Activity_PlayHome_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			send_data_toAPI();
			call_API_Notification();

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (Game_Status) {
				show_toast("Game is Finished");
				button_upload.setEnabled(false);
				button_vote.setEnabled(false);
				hunt_items.setText("");
			} else {
				fb_id_list.clear();
				String st = hunt_name.replaceAll(" ", "\u00A0");

				hunt_items.setText("" + hunt_name);

				if (fb_id_string != null) {
					String[] arr = fb_id_string.split(",");
					for (int i = 0; i < arr.length; i++) {
						if (arr[i].equals(Fb_Id)) {
						} else {
							fb_id_list.add(arr[i]);
						}
					}

				}
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
		Game_Status = false;
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
					Game_Status = true;
				} else {
					hunt_value = json2.getString(TAG_VALUE);
					hunt_id = json2.getString(TAG_HUNT_ID);
					fb_id_string = json2.getString(TAG_FB_ID);

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void send_data_toAPI2() {

		String url = UPLOAD_PICTURE_URL.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		nameValuePairs.add(new BasicNameValuePair("Fb_id", Fb_Id));
		nameValuePairs.add(new BasicNameValuePair("hunt_id", hunt_id));
		nameValuePairs.add(new BasicNameValuePair("picture", image_string));

		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_FILE:
			if (resultCode == RESULT_OK) {
				mImageCaptureUri = data.getData();
				String path = "null";
				if (bigmap != null) {
					bigmap.recycle();
					bigmap = null;
				}

				path = getRealPathFromURI(mImageCaptureUri);
				image_orientation = getOrientation(getApplicationContext(),
						mImageCaptureUri);

				bigmap = reduceImageSize(path);

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						Activity_PlayHome_Class.this);

				// Setting Dialog Title
				alertDialog.setTitle("Upload Picture");

				// Setting Dialog Message
				alertDialog.setMessage("Is this your submission?");

				// Setting Icon to Dialog
				alertDialog.setIcon(R.drawable.scavengericon);

				// Setting Positive "Yes" Button
				alertDialog.setPositiveButton("Send",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								setConnection();
								// Write your code here to invoke YES event
								if (isSession()) {
									new ProgressTaskImageUpload(
											Activity_PlayHome_Class.this)
											.execute();
								} else {
									show_toast("Session Expired Please Login Again");
								}// Toast.makeText(getApplicationContext(),
									// "You clicked on YES",
									// Toast.LENGTH_SHORT).show();
							}
						});

				// Setting Negative "NO" Button
				alertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to invoke NO event
								// Toast.makeText(getApplicationContext(),
								// "You clicked on NO",
								// Toast.LENGTH_SHORT).show();
								dialog.cancel();
							}
						});

				// Showing Alert Message
				alertDialog.show();

			}

			break;
		case TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				getContentResolver().notifyChange(targeturi, null);
				String path = "null";
				path = getRealPathFromURI(mImageCaptureUri);

			}
			break;

		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		if (cursor == null)
			return null;
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public String encodeTobase64(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream); // compress to
																	// which
																	// format
																	// you want.
		byte[] byte_arr = stream.toByteArray();
		String ba1 = BaseConvert.encodeBytes(byte_arr);
		return ba1;
	}

	// Call the above class using this:
	public class ProgressTaskImageUpload extends
			AsyncTask<String, Void, String> {
		Activity_PlayHome_Class activity_login;

		ProgressDialog dialog;

		public ProgressTaskImageUpload(Activity_PlayHome_Class login) {
			activity_login = login;
			dialog = new ProgressDialog(login);
			dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			image_string = encodeTobase64(bigmap);
			send_data_toAPI2();
			// for (int i = 0; i < fb_id_list.size(); i++) {
			// Bundle postParams = new Bundle();
			// postParams.putString("message", Cretor_name
			// + " has submitted a picture for * "
			// + hunt_items.getText().toString()
			// + " *, Go cast your vote in the game. ");
			// mAsyncRunner.request(fb_id_list.get(i) + "/feed", postParams,
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
			dialog.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

	}

	public Bitmap decode(String st) {
		byte[] byte_arr = null;
		Bitmap bit;
		try {
			byte_arr = BaseConvert.decode(st);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bit = BitmapFactory.decodeByteArray(byte_arr, 0, byte_arr.length);
		return bit;
	}

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

			// send_data_toAPI();
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
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

	public void setConnection() {
		mContext = this;
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	}

	public void show_toast(String string) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setTypeface(roboto_medium);
		text.setText(string);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 100);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
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

	private void Image_Upload_Status() {
		// TODO Auto-generated method stub
		image_upload_check = null;
		String url = CAN_UPLOAD_IMAGE_API.replaceAll(" ", "");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("game_id", GAME_ID));
		nameValuePairs.add(new BasicNameValuePair("hunt_id", hunt_id));
		nameValuePairs.add(new BasicNameValuePair("fb_id", Fb_Id));

		// Creating JSON Parser instance
		JSONParserPost jParser = new JSONParserPost();
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url, nameValuePairs);
		try {
			JSONArray result = json.getJSONArray(TAG_IMAGE_UPLOAD);
			for (int i = 0; i < result.length(); i++) {
				// JSONObject json2=result.getJSONObject(i);
				image_upload_check = result.getString(i);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class Progress_Upload_Image_Check extends
			AsyncTask<String, Void, String> {
		Activity_PlayHome_Class activity;

		public Progress_Upload_Image_Check(
				Activity_PlayHome_Class activity_afterlogin) {
			// TODO Auto-generated constructor stub
			activity = activity_afterlogin;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			Image_Upload_Status();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (image_upload_check.equalsIgnoreCase("No")) {
				show_toast("You Can Not Upload Image");
				button_upload.setEnabled(true);

			} else {

				AlertDialog alert = builder.create();
				alert.show();
				button_upload.setEnabled(true);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

	}

	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
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

	public Bitmap reduceImageSize(String mSelectedImagePath) {

		Bitmap bitmap = null;
		Bitmap resultBmp = null;
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		try {
			File f = new File(mSelectedImagePath);

			// <span id="IL_AD3" class="IL_AD">Decode</span> <span id="IL_AD1"
			// class="IL_AD">image size</span>
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 500;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null,
					o2);

			if (image_orientation > 0) {
				Matrix mat = new Matrix();
				mat.postRotate(90);
				resultBmp = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), mat, true);
			} else
				resultBmp = Bitmap.createBitmap(bitmap);
		} catch (FileNotFoundException e) {
			show_toast("Image Not Found");
		} catch (OutOfMemoryError e) {
			show_toast("Out Of Memory");
		}
		return resultBmp;
	}

	public static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

}
