package com.kilomobi.twominutes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nawako on 13/10/2015.
 */
public class General {
    static SharedPreferences sharedpreferences;
    static SharedPreferences.Editor editor;
    static String mMessage;
    static String mMessage2;
    static boolean konamiCode;

    General (Context ctx) {
        mMessage = "nomessage";
        mMessage2 = "nomessage";
        sharedpreferences = ctx.getSharedPreferences("local", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void saveKonami (boolean bool) {
        setKonamiCode(bool);
        editor.putBoolean("konami", bool);
        editor.commit();
    }

    public void saveMessage (String message) {
        setmMessage(message);
        editor.putString("message", message);
        editor.commit();
    }

    public void saveMessage2 (String message) {
        setmMessage2(message);
        editor.putString("message2", message);
        editor.commit();
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmMessage2() {
        return mMessage2;
    }

    public static boolean isKonamiCode() {
        return konamiCode;
    }

    public static void setKonamiCode(boolean konamiCode) {
        General.konamiCode = konamiCode;
    }

    public void setmMessage2(String mMessage2) {
        General.mMessage2 = mMessage2;
    }
}
