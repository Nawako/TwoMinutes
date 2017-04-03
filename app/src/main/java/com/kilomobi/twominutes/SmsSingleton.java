package com.kilomobi.twominutes;

import android.telephony.SmsManager;

/**
 * Created by fabrice on 03/04/2017.
 */

class SmsSingleton {
    private String phoneNumber;

    private static final SmsSingleton ourInstance = new SmsSingleton();

    static SmsSingleton getInstance() {
        return ourInstance;
    }

    private SmsSingleton() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
