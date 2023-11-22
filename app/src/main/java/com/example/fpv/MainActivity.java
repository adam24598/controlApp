package com.example.fpv;
// app/src/main/java/com.example.yourappname/MainActivity.java

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText editTextVideoSource;
    private Button buttonChangeSource;
    private Button buttonUp, buttonDown, buttonLeft, buttonRight,buttonStop;

    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        editTextVideoSource = findViewById(R.id.editTextVideoSource);
        buttonChangeSource = findViewById(R.id.buttonChangeSource);
        buttonUp = findViewById(R.id.buttonUp);
        buttonDown = findViewById(R.id.buttonDown);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);
        buttonStop=findViewById(R.id.buttonStop);

        // Enable JavaScript (if required by the video source)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the default video source
        loadVideoSource("http://your_default_video_source");

        // Set a click listener for the button
        buttonChangeSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the new video source from the EditText
                String newVideoSource = editTextVideoSource.getText().toString();

                // Load the new video source
                loadVideoSource(newVideoSource);
            }
        });

        // Set click listeners for directional buttons
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttCommand("up");
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttCommand("down");
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttCommand("left");
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttCommand("right");
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMqttCommand("stop");
            }
        });

        // Connect to MQTT broker
        connectToMqttBroker();
    }

    private void loadVideoSource(String videoSourceUrl) {
        // Validate the video source URL (you can add more validation logic)
        if (!videoSourceUrl.isEmpty()) {
            // Load the video stream URL
            webView.loadUrl(videoSourceUrl);
        }
    }

    private void connectToMqttBroker() {
        try {
            mqttClient = new MqttClient("tcp://192.168.137.204:1883", MqttClient.generateClientId(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);

            // Subscribe to a topic if needed
            // mqttClient.subscribe("your_topic");

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Handle connection lost
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Handle received message
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Handle delivery complete
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendMqttCommand(String command) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                // Publish the command to a topic
                mqttClient.publish("esp/out", new MqttMessage(command.getBytes()));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
