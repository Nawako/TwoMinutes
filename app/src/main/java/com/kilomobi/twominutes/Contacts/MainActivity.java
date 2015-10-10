package com.kilomobi.twominutes.Contacts;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.kilomobi.twominutes.R;

public class MainActivity extends Activity {
	ArrayList<Contact> listContacts;
	ListView lvContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listContacts = new ContactFetcher(this).fetchAll();
		lvContacts = (ListView) findViewById(R.id.lv_contacts);
		ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts);
		lvContacts.setAdapter(adapterContacts);
	}

}
