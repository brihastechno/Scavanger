package com.mobile.scavenger;

import static com.androidhive.pushnotification.CommonUtilities.SENDER_ID;
import static com.androidhive.pushnotification.CommonUtilities.displayMessage;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.androidhive.pushnotification.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCMIntentService";
	private static String FACEBOOK_ID, NOTIFICATION_MESSAGE;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {

		displayMessage(context, "Your device registred with GCM");
		ServerUtilities.register(context, Activity_MainActivity_Class.FB_id,
				Activity_MainActivity_Class.FB_Name,
				Activity_MainActivity_Class.FB_Profile_pic, registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		Log.e("Message Textxxxxxxxxxxxx", "" + NOTIFICATION_MESSAGE);
		NOTIFICATION_MESSAGE = intent.getExtras().getString("message");
		FACEBOOK_ID = intent.getExtras().getString("fb_id");
		// GAME_ID = intent.getExtras().getString("game_id");
		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				"Session", MODE_PRIVATE);

		String id = pref.getString("face_id", "s");

		if (id.equalsIgnoreCase(FACEBOOK_ID)) {
			generateNotification(context, NOTIFICATION_MESSAGE);
		} else {

		} //

	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {

		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message

		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.scavengericon;
		long when = System.currentTimeMillis();
		int id = (int) System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context,
				Activity_Notification_Class.class);
		notificationIntent.putExtra("message", message);
		notificationIntent.putExtra("fb_id", FACEBOOK_ID);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		notification.defaults |= Notification.DEFAULT_VIBRATE;

		notificationManager.notify((int) System.currentTimeMillis(),
				notification);
	}

	protected void sendnotification(String message) {
		String ns = Context.NOTIFICATION_SERVICE;

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.scavengericon;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.customised_notification);
		contentView.setImageViewResource(R.id.image, R.drawable.scavengericon);
		contentView.setTextViewText(R.id.text, message);

		CharSequence tickerText = message;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.contentView = contentView;

		Intent notificationIntent = new Intent(this,
				Activity_MainActivity_Class.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.contentIntent = contentIntent;

		Random randInt = new Random();
		// int id = randInt.nextInt(100) - 1;
		mNotificationManager.notify((int) System.currentTimeMillis(),
				notification);
	}

}
