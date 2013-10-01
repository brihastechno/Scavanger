package com.mobile.scavenger;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LazyAdapter extends BaseAdapter {

	private Activity_InviteFBFriendList_Class activity;
	private ArrayList<Friend_Users> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	boolean[] checkBoxState;
	public static ArrayList<Boolean> sparseBooleanArray;
	int counter;
	Typeface robot_medium;
	ArrayList<Integer> image_background;

	public LazyAdapter(Activity_InviteFBFriendList_Class a,
			ArrayList<Friend_Users> friends) {
		activity = a;
		data = friends;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		counter = 0;
		sparseBooleanArray = new ArrayList<Boolean>();
		image_background = new ArrayList<Integer>();
		for (int i = 0; i < friends.size(); i++) {
			sparseBooleanArray.add(false);
			Friend_Users friend = data.get(i);
			if (friend.installed == 0) {
				image_background.add(R.drawable.installedback);
			} else if (friend.installed == 1) {
				image_background.add(R.drawable.installedback3);
			} else {
				image_background.add(R.drawable.installback2);
			}
		}
		robot_medium = Typeface.createFromAsset(activity.getAssets(),
				"robotomedium.ttf");

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.custom_friendrow_layout, null);
			holder = new ViewHolder();
			holder.name = (TextView) vi.findViewById(R.id.rowtext_top);
			holder.image = (ImageView) vi.findViewById(R.id.imageView1);
			holder.radio = (RadioButton) vi.findViewById(R.id.radioButton1);
			holder.image_installed = (ImageView) vi
					.findViewById(R.id.imageView_installed);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		Friend_Users friend = data.get(position);
		holder.name.setId(position);
		holder.radio.setId(position);
		holder.image.setId(position);
		holder.image_installed.setId(position);
		holder.name.setTypeface(robot_medium);
		holder.name.setText(friend.name);
		holder.image_installed.setBackgroundResource(image_background
				.get(position));

		holder.radio.setChecked(sparseBooleanArray.get(position));
		imageLoader.DisplayImage(friend.pic_url, holder.image);

		holder.radio.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (sparseBooleanArray.get(position) == false) {
					counter = 0;
					for (int i = 0; i < sparseBooleanArray.size(); i++) {
						if (sparseBooleanArray.get(i) == true)
							counter++;
					}

					if (counter >= 9) {
						Toast.makeText(activity, "Maximum Friends Selected",
								Toast.LENGTH_SHORT).show();
						((RadioButton) v).setChecked(sparseBooleanArray
								.get(position));
					} else {
						sparseBooleanArray.set(position, true);
						((RadioButton) v).setChecked(sparseBooleanArray
								.get(position));
					}

				} else {
					sparseBooleanArray.set(position, false);
					((RadioButton) v).setChecked(sparseBooleanArray
							.get(position));

				}
			}

		});

		return vi;
	}

	public static class ViewHolder {
		public TextView name;

		public ImageView image, image_installed;
		public RadioButton radio;
	}
}