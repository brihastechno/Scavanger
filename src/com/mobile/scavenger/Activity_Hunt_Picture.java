package com.mobile.scavenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Hunt_Picture extends Activity {
	String image_url;
	Bitmap imageBitmap;
	ImageView image;
	Button button_close;
	String FULL_IMAGE_API = "http://mobi-app-licious.com/scavangerapi/gcm_send_notification/game-fullpic_detail_cal.php";
	static Typeface roboto_medium;
	ImageLoaderFull imageLoader;
	ProgressDialog dialog;

	// String FULL_IMAGE_API =
	// "http://brstdev.com/scavanger/gcm_send_notification/game-fullpic_detail_cal.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_hunt_picture_layout);

		image = (ImageView) findViewById(R.id.imageView1);
		image.setVisibility(8);
		image_url = getIntent().getStringExtra("image_url");
		button_close = (Button) findViewById(R.id.button1);
		roboto_medium = Typeface.createFromAsset(getAssets(),
				"robotomedium.ttf");
		imageLoader = new ImageLoaderFull(getApplicationContext());
		button_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		dialog = new ProgressDialog(Activity_Hunt_Picture.this);
		imageLoader.DisplayImage(image_url, image);
		// button_close.setVisibility(0);

		new ProgressTask(Activity_Hunt_Picture.this).execute();
	}

	public class ProgressTask extends AsyncTask<String, Void, String> {
		Activity_Hunt_Picture activity_login;

		public ProgressTask(Activity_Hunt_Picture login) {
			activity_login = login;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			while (image.getVisibility() == 8) {

			}

			// try {
			// InputStream is = new URL(image_url).openStream();
			//
			// imageBitmap = BitmapFactory.decodeStream(is);
			//
			// is.close();
			// } catch (MalformedURLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// catch (OutOfMemoryError e) {
			// // TODO Auto-generated catch block
			//
			// e.printStackTrace();
			// }

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// image.setImageBitmap(imageBitmap);

			dialog.cancel();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
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
		text.setTypeface(roboto_medium);
		text.setText(string);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 100);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ImageLoader image = new ImageLoader(getApplicationContext());
		image.clearCache();
	}

}
