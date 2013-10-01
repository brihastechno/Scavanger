package com.mobile.scavenger;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter_LeaderBoard extends BaseAdapter {

	private Activity_LeaderBoard_Class activity;
	private ArrayList<Leaderboard_persons> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	boolean[] checkBoxState;
	public static ArrayList<Boolean> sparseBooleanArray;
	int counter;
	Typeface robot_medium;
	ArrayList<Integer> image_background;

	public LazyAdapter_LeaderBoard(Activity_LeaderBoard_Class a,
			ArrayList<Leaderboard_persons> friends) {
		activity = a;
		data = friends;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());

		sparseBooleanArray = new ArrayList<Boolean>();

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
			vi = inflater.inflate(R.layout.custom_leaderboard_row, null);
			holder = new ViewHolder();
			holder.name = (TextView) vi.findViewById(R.id.rowtext_top);
			holder.image = (ImageView) vi.findViewById(R.id.imageView1);
			holder.points = (TextView) vi.findViewById(R.id.text_points);

			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		Leaderboard_persons persons = data.get(position);
		holder.name.setId(position);
		holder.points.setId(position);
		holder.image.setId(position);
		holder.name.setTypeface(robot_medium);
		holder.name.setText(persons.name);
		imageLoader.DisplayImage(persons.pic_url, holder.image);
		return vi;
	}

	public static class ViewHolder {
		public TextView name, points;
		public ImageView image;

	}
}