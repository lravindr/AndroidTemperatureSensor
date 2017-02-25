package com.minmax.sensor.temperaturereading;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

/**
 * Created by lravindr on 2/25/17.
 */

public class QueueDataPuller {

    private static final String TAG = "SQSActivity";
    /* SQS queue URL */
    private String queueURL = "https://sqs.us-east-1.amazonaws.com/060965138370/current-temperature";
    /* Cognito Identity Pool ID */
    private String cognitoPoolID = "us-east-1:6a4e17f4-4072-4d5f-a5ab-3130e88de111";
    /* SQS queue */
    private AmazonSQS sqs;
    /* Current temperature */
    private String currentTemperature;
    /* Caching file name */
    public static final String TEMP_CACHE_NAME = "TemperatureCacheFile";
    /* Caching string */
    public static final String cachedTemperature = "temperature";

    public QueueDataPuller(Context appContext) {
        // TODO: Gather the cognito credentials from the User rather than initializing deep inside the config
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                appContext,
                cognitoPoolID,
                Regions.US_EAST_1
        );

        // Initialize sqs client
        sqs = new AmazonSQSClient(credentialsProvider);
        sqs.setRegion(Region.getRegion(Regions.US_EAST_1));

        Log.i(TAG, "Initialized SQS Client with Cognito credentials");

        SharedPreferences settings = appContext.getSharedPreferences(TEMP_CACHE_NAME, Context.MODE_PRIVATE);
        currentTemperature = settings.getString(cachedTemperature, null);
        Log.i(TAG, "Initialized temperature from app cache or set to null: " + currentTemperature);
    }

    public String getCurrentTemperature(Context appContext) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        Log.i(TAG, "Received messages from queue");

        if (!messages.isEmpty()) {
            // Show only the first message. Queue is always FIFO.
            Message message = messages.get(0);
            Log.d(TAG, "Body of the message: " + message.getBody());
            currentTemperature = message.getBody();
            String messageReceiptHandle = message.getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(queueURL, messageReceiptHandle));
            Log.i(TAG, "Deleting message: " + messageReceiptHandle);

            // Cache the existing temperature
            SharedPreferences settings = appContext.getSharedPreferences(TEMP_CACHE_NAME, Context.MODE_PRIVATE);
            Editor editor = settings.edit();
            editor.putString(cachedTemperature, currentTemperature);
            editor.commit();
            Log.i(TAG, "Setting temperature in app cache: " + currentTemperature);
        } else {
            Log.d(TAG, "Queue is empty");
        }

        return currentTemperature;
    }
}