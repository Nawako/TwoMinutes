package com.kilomobi.twominutes.Contacts;

import java.util.ArrayList;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kilomobi.twominutes.*;

public class ContactsAdapter extends ArrayAdapter<Contact> {

	// Save the clicked favorite contact
	private int mFavoriteID = -1;
	private String mFavoritePhoneNumber = "-1";
	// Get the last row item to change color
	private RelativeLayout lastRelativeLayout;
	private TextView lastTextView;

	public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
		super(context, 0, contacts);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// Get the data item
		Contact contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.cell, parent, false);
		}
		// Get the relativeLayout to change his bakcground color when clicked
		final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.contact_id);
		// Populate the data into the template view using the data object
		TextView tvName = (TextView) view.findViewById(R.id.contact_name);
	//	TextView tvEmail = (TextView) view.findViewById(R.id.contact_email);
		final TextView tvPhone = (TextView) view.findViewById(R.id.contact_number);

		if (position != mFavoriteID) {
			// change color of latest row
			relativeLayout.setBackgroundResource(R.color.dim_foreground_material_dark);
			tvPhone.setTextColor(getContext().getResources().getColor(R.color.blue_normal));
			// set defaults colors
			relativeLayout.setBackgroundResource(R.color.dim_foreground_material_dark);
			tvPhone.setTextColor(getContext().getResources().getColor(R.color.blue_normal));
		} else {
			// set favorites colors
			relativeLayout.setBackgroundResource(R.color.blue_normal);
			tvPhone.setTextColor(getContext().getResources().getColor(R.color.background_material_light));
		}

		tvName.setText(contact.name);
	//	tvEmail.setText("");
		tvPhone.setText("");
	/*	if (contact.emails.size() > 0 && contact.emails.get(0) != null) {
			tvEmail.setText(contact.emails.get(0).address);
		}
	*/
		if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}

		// Click listener
		View.OnClickListener clickedContact = new View.OnClickListener() {
			public void onClick(View v) {

				if (mFavoriteID != position) {
					try {
						lastRelativeLayout.setBackgroundResource(R.color.dim_foreground_material_dark);
						lastTextView.setTextColor(getContext().getResources().getColor(R.color.blue_normal));
					} catch (NullPointerException e) { }
				}
				// set the row as the latest
				lastRelativeLayout = relativeLayout;
				lastTextView = tvPhone;

				// getPhone
				mFavoritePhoneNumber = (String) tvPhone.getText();

				// change color of actual row
				relativeLayout.setBackgroundResource(R.color.blue_normal);
				tvPhone.setTextColor(getContext().getResources().getColor(R.color.background_material_light));
				mFavoriteID = position;
			}
		};

		// Assigne le click au click listener
		view.setOnClickListener(clickedContact);

		return view;
	}

	public String getmFavoritePhoneNumber() {
		return mFavoritePhoneNumber;
	}

	public int getmFavoriteID() {
		return mFavoriteID;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	@Override
	public boolean isEnabled(int arg0)
	{
		return true;
	}
}
