package com.minmax.sensor.temperaturereading;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.os.AsyncTask;

import android.app.Activity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.temperaturebutton);
        final Activity activity = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TemperatureTask().execute(activity);
            }
        });
    }

    class TemperatureTask extends AsyncTask<Context, Void, Void> {
        private String currentTemperature;

        @Override
        protected Void doInBackground(Context... context) {
            final QueueDataPuller queueDataPuller = new QueueDataPuller(context[0]);
            currentTemperature = queueDataPuller.getCurrentTemperature();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final TextView textViewTemperature = (TextView) findViewById(R.id.temperaturetext);
            textViewTemperature.setText(currentTemperature + "\u00B0" +"C");
        }
    }
}
