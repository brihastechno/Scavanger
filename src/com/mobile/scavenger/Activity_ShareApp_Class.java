package com.mobile.scavenger;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ShareApp_Class extends Activity {
	private Button button_back, button_send, button_contacts;
	public static final int PICK_CONTACT = 12;
	EditText edittext_number;
	String[] numbers;
	Typeface robot_medium;
	TextView textview_message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_shareapp_layout);
		robot_medium = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
		textview_message = (TextView) findViewById(R.id.text_message);
		button_back = (Button) findViewById(R.id.button_back);
		button_send = (Button) findViewById(R.id.button_send);
		button_contacts = (Button) findViewById(R.id.button_contacts);
		edittext_number = (EditText) findViewById(R.id.editText_number);
		edittext_number.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		button_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		button_contacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT);
			}
		});
		button_send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edittext_number.getText().toString().length() < 1) {
					show_toast("Contact Number not Selected");
				} else {
					send();
				}
			}
		});
		textview_message.setTypeface(robot_medium);
		button_send.setTypeface(robot_medium);
		button_contacts.setTypeface(robot_medium);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			finish();
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT) {
			numbers = null;
			int i = 0;

			String phoneNumber = null;
			String id = null;
			String name = null;

			ContentResolver cr = getContentResolver();
			Cursor cur = cr.query(data.getData(), null, null, null, null);

			if (cur.getCount() > 0) {

				while (cur.moveToNext()) {
					id = cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts._ID));

					name = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					if (Integer
							.parseInt(cur.getString(cur
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " =?", new String[] { id },
										null);
						numbers = new String[pCur.getCount()];
						while (pCur.moveToNext()) {
							phoneNumber = pCur
									.getString(pCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							numbers[i] = phoneNumber;
							i++;
						}
						pCur.close();
					}
				}

			}
			cur.close();
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle(name);
			ad.setItems(numbers, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					edittext_number.setText(numbers[which]);
				}
			});
			AlertDialog adb = ad.create();
			adb.show();
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

	// this is the function that gets called when you click the button
	public void send() {
		// get the phone number from the phone number text field
		String phoneNumber = edittext_number.getText().toString();
		// get the message from the message text box
		String msg = textview_message.getText().toString();

		// make sure the fields are not empty
		if (phoneNumber.length() > 0 && msg.length() > 0) {
			// call the sms manager

			// PendingIntent pi =
			// PendingIntent.getActivity(Activity_ShareApp_Class.this, 0, new
			// Intent(),
			// 0);
			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> parts = sms.divideMessage(msg);
			// this is the function that does all the magic
			sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
			// Intent i=new Intent();
			edittext_number.setText("");
			show_toast("Message Sent");

		} else {
			show_toast("Required Fields");
		}

	}

}
