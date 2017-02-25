package com.minmax.sensor.temperaturereading;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.temperaturebutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TemperatureTask().execute();
            }
        });
    }

    class TemperatureTask extends AsyncTask<Void, Void, Void> {
        private String currentTemperature;

        @Override
        protected Void doInBackground(Void... v) {
            final QueueDataPuller queueDataPuller = new QueueDataPuller(getApplicationContext());
            currentTemperature = queueDataPuller.getCurrentTemperature(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            final TextView textViewTemperature = (TextView) findViewById(R.id.temperaturetext);
            textViewTemperature.setText(currentTemperature + "\u00B0" + "C");
        }
    }
}
