package com.minmax.sensor.temperaturereading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.os.StrictMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Convert to async task (http://stackoverflow.com/questions/22395417/error-strictmodeandroidblockguardpolicy-onnetwork)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.temperaturebutton);
        final TextView textViewTemperature = (TextView) findViewById(R.id.temperaturetext);
        final QueueDataPuller queueDataPuller = new QueueDataPuller(this);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textViewTemperature.setText(queueDataPuller.getCurrentTemperature() + (char) 0x00B0 + "C");
            }
        });
    }
}
