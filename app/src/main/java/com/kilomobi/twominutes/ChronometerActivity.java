package com.kilomobi.twominutes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kilomobi.twominutes.Chronometer.AnalogChronometer;
import com.kilomobi.twominutes.ProgressGenerator.ProgressGenerator;

/**
 * Created by macbookpro on 06/10/2015.
 */
public class ChronometerActivity extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {

    AnalogChronometer chronometer;
    Button btnQuit, btnRedo;
    ActionProcessButton btnArrive;
    Activity mActivity;
    Context mContext;
    boolean isArrived;
    final SmsSingleton singleton = SmsSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long value = intent.getLongExtra("chrono", SystemClock.elapsedRealtime()); //if it's a string you stored.

        setContentView(R.layout.chronometer);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        chronometer = (AnalogChronometer) findViewById(R.id.aChronometer);
        chronometer.setBase(value);
        chronometer.start();

        mActivity = this;
        mContext = this;

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final TextView tvArrive = (TextView) findViewById(R.id.tvArrive);
        final TextView tvThanks = (TextView) findViewById(R.id.tvThanks);
        btnQuit = (Button) findViewById(R.id.btn_quit);
        btnRedo = (Button) findViewById(R.id.btn_redo);
        btnArrive = (ActionProcessButton) findViewById(R.id.btn_arrive);

        btnArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isArrived) {
                    chronometer.stop();
                    btnArrive.setText(R.string.arrive_btn_text_send);
                    tvArrive.setVisibility(View.VISIBLE);
                    tvThanks.setVisibility(View.VISIBLE);
                    btnRedo.setVisibility(View.VISIBLE);
                    btnQuit.setVisibility(View.VISIBLE);
                    if (chronometer.getMinutes() > 0)
                        tvArrive.setText(String.format(getResources().getString(R.string.arrive2), chronometer.getMinutes(), chronometer.getSeconds()));
                    else
                        tvArrive.setText(String.format(getResources().getString(R.string.arrive), chronometer.getSeconds()));

                    isArrived = true;
                } else {
                    progressGenerator.start(btnArrive);
                    btnArrive.setText(R.string.arrived_btn_text);
                    singleton.sendSMS(singleton.getPhoneNumber(), getString(R.string.arrive_sms_tosend));
                    btnArrive.setEnabled(false);
                }

            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mContext, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    @Override
    public void onComplete() {
        btnArrive.setText(R.string.arrive_sms_completed);
    }
}
