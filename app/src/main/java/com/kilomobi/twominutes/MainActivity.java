package com.kilomobi.twominutes;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.dd.processbutton.iml.SubmitProcessButton;
import com.kilomobi.twominutes.Contacts.Contact;
import com.kilomobi.twominutes.Contacts.ContactFetcher;
import com.kilomobi.twominutes.Contacts.ContactsAdapter;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, ProgressGenerator.OnCompleteListener {

    ArrayList<Contact> listContacts;
    ListView mListView;
    myContactAdapter adapter;
    public  MainActivity customListView = null;
    public ArrayList<ContactModel> CustomListViewValuesArr = new ArrayList<ContactModel>();

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
    // and name should be displayed in the text1 textview in item layout
    private static final String[] FROM = { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts._ID };
 //   private static final int[] TO = { android.R.id.text1 };
    private static final int[] TO = { R.id.contact_name, R.id.contact_number };
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID, // _ID is always required
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, // that's what we want to display
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        duree = 0;
        btn_pressed = false;

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
                dureeState(btn15);
            }
        });

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final SubmitProcessButton btnValider = (SubmitProcessButton) findViewById(R.id.btn_valider);
        chronometer = new AnalogChronometer(this);

    //    getContacts();

        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkIfButtonPressed()) {
                    btnValider.setError(null);
                    btnValider.setEnabled(false);
            //        sendSMS(item_Phone, "Attention !!! J'arrive dans " + duree + " minutes !");
                    // no progress
                    progressGenerator.start(btnValider);
                    chronometer.start();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                }
                else {
                    btnValider.setError("duree");
                    Toast.makeText(mContext, "Choisir une durée !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // create adapter once
    //    int layout = android.R.layout.simple_list_item_1;
        int layout = R.layout.cell;
        Cursor c = null; // there is no cursor yet
        int flags = 0; // no auto-requery! Loader requeries.
        // and tell loader manager to start loading
        getLoaderManager().initLoader(0, null, this);

        customListView = this;

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
   //     setListData();

        /**************** Create Custom Adapter *********/
        listContacts = new ContactFetcher(this).fetchAll();
        ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts);
        mListView.setAdapter(adapterContacts);

   //     adapter=new myContactAdapter( customListView, CustomListViewValuesArr);
   //     mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                int item_ID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String item_DisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int item_HasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                String item_LookUp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                item_Phone = "";

                String item_PhoneNumber = "";
                if (item_HasPhoneNumber > 0){
                    item_PhoneNumber = "Has phone number.";

                    // New stuff
                    ContentResolver cr = getContentResolver();
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + item_ID, null, null);
                    while (phones.moveToNext()) {
                        ContactModel contactModel = new ContactModel();

                        item_Phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                        contactModel.setContactPhone(item_Phone);
                        contactModel.setContactName(item_DisplayName);

                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                // do something with the Home number here...
                                Log.i("TYPEHOME","ok");
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                // do something with the Mobile number here...
                                Log.i("TYPEMOBILE","ok");
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                // do something with the Work number here...
                                Log.i("TYPEWORK","ok");
                                break;
                        }
                        CustomListViewValuesArr.add(contactModel);
                    }
                    phones.close();

                }else{
                    item_PhoneNumber = "No number.";
                }

                String item = String.valueOf(item_ID) + ": " + item_DisplayName
                        + "\n" + item_PhoneNumber
                        + "\nLOOKUP_KEY: " + item_LookUp
                        + "\nPHONE: " + item_Phone;
                Toast.makeText(mContext, "C'est bon.", Toast.LENGTH_SHORT).show();
            }
        });



        mCursorAdapter = new SimpleCursorAdapter(this, layout, c, FROM, TO, flags);
    //    myContactAdapter mCursorAdapterB = new myContactAdapter(customListView, CustomListViewValuesArr);
    //    mListView.setAdapter(mCursorAdapterB);
    }

    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection =
                new String[]{ ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
                " COLLATE LOCALIZED ASC";
        Cursor myQuery = managedQuery(uri, projection, selection, selectionArgs, sortOrder);
        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
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
        return true;
    }

    /****** Function to set data in ArrayList *************/
    public void setListData()
    {

        for (int i = 0; i < 11; i++) {

            final ContactModel sched = new ContactModel();

            /******* Firstly take data in model object ******/
            sched.setContactName("contact"+i);
            sched.setContactPhone("image"+i);

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add( sched );
        }

    }

    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        ContactModel mContact = ( ContactModel ) CustomListViewValuesArr.get(mPosition);


        // SHOW ALERT

        Toast.makeText(customListView,
                ""+mContact.getContactName(),
        Toast.LENGTH_LONG)
        .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // load from the "Contacts table"
        Uri contentUri = ContactsContract.Contacts.CONTENT_URI;

        // no sub-selection, no sort order, simply every row
        // projection says we want just the _id and the name column
        return new CursorLoader(this,
                contentUri,
                PROJECTION,
                "starred=?",
                new String[] {"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Once cursor is loaded, give it to adapter
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // on reset take any old cursor away
        mCursorAdapter.swapCursor(null);
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
