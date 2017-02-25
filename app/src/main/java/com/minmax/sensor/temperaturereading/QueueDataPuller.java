package com.minmax.sensor.temperaturereading;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;

import java.util.List;

import android.util.Log;

/**
 * Created by lravindr on 2/25/17.
 */

public class QueueDataPuller {

    /* SQS queue URL */
    private String queueURL = "https://sqs.us-east-1.amazonaws.com/060965138370/current-temperature";

    /* Cognito Identity Pool ID */
    private String cognitoPoolID = "us-east-1:6a4e17f4-4072-4d5f-a5ab-3130e88de111";

    /* SQS queue */
    private AmazonSQS sqs;

    private static final String TAG = "MyActivity";

    public QueueDataPuller(Context appContext) {
        // TODO: Gather the cognito credentials from the User rather than initializing deep inside the config
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                appContext,
                cognitoPoolID, // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        // Initialize sqs client
        sqs = new AmazonSQSClient(credentialsProvider);
        sqs.setRegion(Region.getRegion(Regions.US_EAST_1));

        Log.i(TAG, "Initialized SQS Client with Cognito credentials");
    }

    public String getCurrentTemperature() {
        String content = null;
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        Log.i(TAG, "Received messages from queue");

        if (!messages.isEmpty()) {
            // Show only the first message. Queue is always FIFO. If the objective is LIFO, this method
            // needs significant changes
            Message message = messages.get(0);
            Log.d(TAG, "Body of the message: " + message.getBody());
            content = message.getBody();
            String messageReceiptHandle = message.getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(queueURL, messageReceiptHandle));
            Log.i(TAG, "Deleting message: " + messageReceiptHandle);

        } else {
            // TODO: Should introduce a caching technique
            // If the message queue is empty, it only means that the queue does not have any more messages,
            // or all the messages are in flight
            Log.d(TAG, "Queue is empty");
        }


        return content;
    }
}