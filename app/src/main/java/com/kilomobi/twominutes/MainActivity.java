package com.kilomobi.twominutes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements ProgressGenerator.OnCompleteListener {

    ArrayList<Contact> listContacts;
    ListView mListView;
    public  MainActivity customListView = null;
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 5;
    final int MY_PERMISSIONS_REQUEST_SEND_SMS = 6;

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
        final SmsSingleton smsSingleton = SmsSingleton.getInstance();

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
                if (doesUserHavePermission(Manifest.permission.SEND_SMS)) {
                    if (checkIfButtonPressed() && checkIfContactSelected()) {
                        btnValider.setError(null);
                        btnValider.setEnabled(false);
                        smsSingleton.setPhoneNumber(adapterContacts.getmFavoritePhoneNumber());
                        if (general.sharedpreferences.getString("message", "nomessage") == "nomessage") {
                            smsSingleton.sendSMS(adapterContacts.getmFavoritePhoneNumber(),
                                    String.format(getResources().getString(R.string.message),
                                            duree));
                        }
                        else if (general.sharedpreferences.getBoolean("konami", false))
                            smsSingleton.sendSMS(adapterContacts.getmFavoritePhoneNumber(),
                                    general.sharedpreferences.getString("message", "nomessage") +
                                            " " + duree + " minutes " +
                                            general.sharedpreferences.getString("message2", "nomessage"));
                        else {
                            smsSingleton.sendSMS(adapterContacts.getmFavoritePhoneNumber(),
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
                else {
                    requestPermission(Manifest.permission.SEND_SMS, MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
        });

        customListView = this;

        /**************** Create Custom Adapter *********/
        if (doesUserHavePermission(Manifest.permission.READ_CONTACTS)) {
            fetchContact();
        } else {
            requestPermission(Manifest.permission.READ_CONTACTS, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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

    private boolean doesUserHavePermission(String permission)
    {
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int idPermission) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        idPermission);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        idPermission);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl_main);
        int customViewTag = 42;
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        fetchContact();
                        if (rl.findViewWithTag(customViewTag) != null) {
                            rl.removeView(rl.findViewWithTag(customViewTag));
                        }
                    } else {
                        if (rl.findViewWithTag(customViewTag) == null) {
                            // ----------------------------------------------------------------------
                            // Placement dans un linear layout pour fixer la permission via un bouton
                            // ----------------------------------------------------------------------
                            LinearLayout llPermission = new LinearLayout(this);
                            llPermission.setOrientation(LinearLayout.HORIZONTAL);
                            llPermission.setTag(customViewTag);

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                            params.topMargin = 120;

                            // ----------------------------------------------------------------------
                            // FlatButton pour fix la permission
                            // ----------------------------------------------------------------------
                            FlatButton btnFix = new FlatButton(this);
                            btnFix.setText("Fix");
                            btnFix.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
                            btnFix.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermission(Manifest.permission.READ_CONTACTS, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                }
                            });
                            llPermission.addView(btnFix);

                            // ----------------------------------------------------------------------
                            // Text pour expliquer à l'user
                            // ----------------------------------------------------------------------
                            TextView text = new TextView(this);
                            text.setText("This app require the contacts (read).");
                            text.setPadding(15,0,15,0);
                            llPermission.addView(text);

                            rl.addView(llPermission, params);
                        }
                    }
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
    }

    private void fetchContact() {
        listContacts = new ContactFetcher(this).fetchAll();
        adapterContacts = new ContactsAdapter(this, listContacts);
        mListView.setAdapter(adapterContacts);

        if (listContacts.size() == 0) {
            Toast.makeText(mContext, getResources().getString(R.string.no_contacts), Toast.LENGTH_LONG).show();
        }
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
