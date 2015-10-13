package com.kilomobi.twominutes;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.iml.SubmitProcessButton;
import com.kilomobi.twominutes.Chronometer.AnalogChronometer;
import com.kilomobi.twominutes.Contacts.Contact;
import com.kilomobi.twominutes.Contacts.ContactFetcher;
import com.kilomobi.twominutes.Contacts.ContactsAdapter;
import com.kilomobi.twominutes.ProgressGenerator.ProgressGenerator;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements ProgressGenerator.OnCompleteListener {

    ArrayList<Contact> listContacts;
    ListView mListView;
    public  MainActivity customListView = null;

    int duree;
    boolean btn_pressed;
    String item_Phone;
    FlatButton btn2;
    FlatButton btn5;
    FlatButton btn10;
    FlatButton btn15;
    SimpleCursorAdapter mCursorAdapter;
    Context mContext;
    AnalogChronometer chronometer;
    ContactsAdapter adapterContacts;
    int konamiCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        duree = 0;
        btn_pressed = false;

        konamiCode = 7;

        final General general = new General(mContext);
        /*
        // Drawer activity
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Drawer Item Home!");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withName("Message");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withName("Drawer Item 3 !");

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        new SecondaryDrawerItem().withName("Secondary Drawer Item!")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Toast.makeText(mContext, "trololo", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .build();
           */

        // Show the hamburger icon
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        mListView = (ListView) findViewById(R.id.lv_contacts);
        btn2 = (FlatButton)findViewById(R.id.btn_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duree = 2;
                dureeState(btn2);
            }
        });
        btn5 = (FlatButton)findViewById(R.id.btn_5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duree = 5;
                dureeState(btn5);
            }
        });
        btn10 = (FlatButton)findViewById(R.id.btn_10);
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duree = 10;
                dureeState(btn10);
            }
        });
        btn15 = (FlatButton)findViewById(R.id.btn_15);
        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duree = 15;
                konamiCode = konamiCode-1;
                if (konamiCode == 0) {
                    Toast.makeText(mContext, "Konami code activated", Toast.LENGTH_SHORT).show();
                    general.saveKonami(true);
                }
                dureeState(btn15);
            }
        });

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final SubmitProcessButton btnValider = (SubmitProcessButton) findViewById(R.id.btn_valider);
        chronometer = new AnalogChronometer(this);
        
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfButtonPressed() && checkIfContactSelected()) {
                    btnValider.setError(null);
                    btnValider.setEnabled(false);
                    if (general.sharedpreferences.getString("message", "nomessage") == "nomessage") {
                        sendSMS(adapterContacts.getmFavoritePhoneNumber(),
                                String.format(getResources().getString(R.string.message),
                                        duree));
                    }
                    else if (general.sharedpreferences.getBoolean("konami", false))
                        sendSMS(adapterContacts.getmFavoritePhoneNumber(),
                                general.sharedpreferences.getString("message", "nomessage") +
                                        " " + duree + " minutes " +
                                        general.sharedpreferences.getString("message2", "nomessage"));
                    else {
                        sendSMS(adapterContacts.getmFavoritePhoneNumber(),
                                general.sharedpreferences.getString("message", "nomessage") +
                                        " " + duree + " minutes " +
                                general.sharedpreferences.getString("message2", "nomessage") +
                                getResources().getString(R.string.send_with));
                    }
                    // no progress
                    progressGenerator.start(btnValider);
                    chronometer.start();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                } else {
                    btnValider.setError("duree");
                    Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        customListView = this;

        /**************** Create Custom Adapter *********/
        listContacts = new ContactFetcher(this).fetchAll();
        adapterContacts = new ContactsAdapter(this, listContacts);
        mListView.setAdapter(adapterContacts);

        if (listContacts.size() == 0) {
            Toast.makeText(mContext, getResources().getString(R.string.no_contacts), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onComplete() {
        Intent myIntent = new Intent(this, ChronometerActivity.class);
        myIntent.putExtra("chrono", chronometer.getBase());
        startActivity(myIntent);
        finish();
    }

    private void dureeState (FlatButton mButton) {
        btn2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        btn5.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        btn10.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        btn15.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        mButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        btn_pressed = true;
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    boolean checkIfButtonPressed () {
        return btn_pressed;
    }

    boolean checkIfContactSelected () {
        if (adapterContacts.getmFavoriteID() == -1)
            return false;
        else
            return true;
    }

    public void settingClicked (View v) {
        Intent myIntent = new Intent(this, SettingScreen.class);
        startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
