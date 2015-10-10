package com.kilomobi.twominutes.Contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// new ContactFetcher(this).fetchAll();
public class ContactFetcher {

    private final Context context;
    private final String criteria_fav;
    private final String[] criteria_arg;

    public ContactFetcher(Context c) {
        this.context = c;
        criteria_fav =  "starred=?";
        criteria_arg = new String[] {"1"};
    }

    public ArrayList<Contact> fetchAll() {
        String[] projectionFields = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
        };
        ArrayList<Contact> listContacts = new ArrayList<>();
        CursorLoader cursorLoader = new CursorLoader(context,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields, // the columns to retrieve
                criteria_fav, // the selection criteria (favorites contacts)
                criteria_arg, // the selection args (1 = true)
                null // the sort order (default)
        );

        Cursor c = cursorLoader.loadInBackground();

        final Map<String, Contact> contactsMap = new HashMap<>(c.getCount());

        if (c.moveToFirst()) {

            int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            do {
                String contactId = c.getString(idIndex);
                String contactDisplayName = c.getString(nameIndex);
                Contact contact = new Contact(contactId, contactDisplayName);
                contactsMap.put(contactId, contact);
                listContacts.add(contact);
            } while (c.moveToNext());
        }

        c.close();

        matchContactNumbers(contactsMap);
    //    matchContactEmails(contactsMap);

        return listContacts;
    }

    public void matchContactNumbers(Map<String, Contact> contactsMap) {
        // Get numbers
        final String[] numberProjection = new String[]{
                Phone.NUMBER,
                Phone.TYPE,
                Phone.CONTACT_ID,
        };

        Cursor phone = new CursorLoader(context,
                Phone.CONTENT_URI,
                numberProjection,
                criteria_fav,
                criteria_arg,
                null).loadInBackground();

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);
            final int contactIdColumnIndex = phone.getColumnIndex(Phone.CONTACT_ID);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                final String contactId = phone.getString(contactIdColumnIndex);
                Contact contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                final int type = phone.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, customLabel);
                contact.addNumber(number, phoneType.toString());
                phone.moveToNext();
            }
        }

        phone.close();
    }

    public void matchContactEmails(Map<String, Contact> contactsMap) {
        // Get email
        final String[] emailProjection = new String[]{
                Email.DATA,
                Email.TYPE,
                Email.CONTACT_ID,
        };

        Cursor email = new CursorLoader(context,
                Email.CONTENT_URI,
                emailProjection,
                criteria_fav,
                criteria_arg,
                null).loadInBackground();

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);
            final int contactIdColumnsIndex = email.getColumnIndex(Email.CONTACT_ID);

            while (!email.isAfterLast()) {
                final String address = email.getString(contactEmailColumnIndex);
                final String contactId = email.getString(contactIdColumnsIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                Contact contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                CharSequence emailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(), type, customLabel);
                contact.addEmail(address, emailType.toString());
                email.moveToNext();
            }
        }

        email.close();
    }
}
