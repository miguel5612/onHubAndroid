package com.example.onhub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStream;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    Button btnconectar, button2;
    private CustomGauge gauge1;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gauge1 = findViewById(R.id.gauge1);
        button2 = (Button)findViewById(R.id.button2);
        btnconectar = (Button)findViewById(R.id.btnconectar);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        for (i=0;i<100;i++) {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        gauge1.setValue(i*10);
                                    }
                                });
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        });

        btnconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String topic = "droneFenix/2/estacion1";
                int qos = 1;
                String clientId = MqttClient.generateClientId();
                final MqttAndroidClient client =
                        new MqttAndroidClient(MainActivity.this, "tcp://test.mosquitto.org:1883",clientId);
                try {
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                            subscribe(client,topic);
                            client.setCallback(new MqttCallback() {
                                @Override
                                public void connectionLost(Throwable cause) {

                                }

                                @Override
                                public void messageArrived(String topic, MqttMessage message) throws Exception {
                                    Log.d("mensaje", message.toString());
                                }

                                @Override
                                public void deliveryComplete(IMqttDeliveryToken token) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d("Erro conexion",exception.getMessage());
                            Toast.makeText(MainActivity.this, "not connected", Toast.LENGTH_SHORT).show();

                        }
                    });


                } catch (MqttException e) {
                    e.printStackTrace();
                }


            }
        });

    }
    public void subscribe(MqttAndroidClient client , String topic)
    {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
