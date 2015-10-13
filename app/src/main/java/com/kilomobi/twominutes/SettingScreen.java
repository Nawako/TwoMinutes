package com.kilomobi.twominutes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.w3c.dom.Text;

/**
 * Created by Nawako on 13/10/2015.
 */
public class SettingScreen extends AppCompatActivity {
    EditText mInputText;
    EditText mInputText2;
    TextView mOutputText;
    General general;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        general = new General(this);

        mInputText = (EditText) findViewById(R.id.edit_input_text);
        mInputText2 = (EditText) findViewById(R.id.edit_input_text2);
        mOutputText = (TextView) findViewById(R.id.tvCredits);

        if (general.sharedpreferences.getString("message","nomessage") == "nomessage") {
            mInputText.setText(getResources().getString(R.string.message_base));
            mInputText2.setText(" !");
        }
        else {
            mInputText.setText(general.sharedpreferences.getString("message", "nomessage"));
            mInputText2.setText(general.sharedpreferences.getString("message2", ""));
        }

        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String mText = mInputText.getText().toString() + " [MINUTES] " + mInputText2.getText().toString();
                mOutputText.setText(mText);
                general.saveMessage(mInputText.getText().toString());
                general.saveMessage2(mInputText2.getText().toString());
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        mInputText.addTextChangedListener(inputTextWatcher);
        mInputText2.addTextChangedListener(inputTextWatcher);

        // Anim credit's text
        YoYo.with(Techniques.FadeIn).duration(3000).playOn(findViewById(R.id.tvCredits));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backButton (View v) {
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
