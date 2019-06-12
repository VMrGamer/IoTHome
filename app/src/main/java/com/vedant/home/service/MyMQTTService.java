package com.vedant.home.service;

import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.vedant.home.MainActivity;
import com.vedant.home.R;
import com.vedant.home.helpers.AppData;
import com.vedant.home.helpers.GlobalNotificationBuilder;
import com.vedant.home.helpers.PahoMQTTClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMQTTService extends Service {
    private static final String TAG = MyMQTTService.class.getSimpleName();

    private PahoMQTTClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;

    public MyMQTTService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service Started");

        pahoMqttClient = new PahoMQTTClient();
        mqttAndroidClient = pahoMqttClient.getMqttAndroidClient(
                getApplicationContext(),
                AppData.MQTT_BROKER_URL,
                AppData.CLIENT_ID,
                AppData.MQTT_USERNAME,
                AppData.MQTT_PASSWORD);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                setMessageNotification(topic, new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service Stopped");
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String message) {
        NotificationCompat.Builder builder = GlobalNotificationBuilder.getNotificationCompatBuilderInstance()
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(topicResolver(topic))
                .setContentText(messageResolver(topic, message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(MainActivity.NOTIFICATION_ID, builder.build());
    }

    private String topicResolver(String topic){
        return topic;
    }

    private String messageResolver(String topic, String message){
        return message;
    }
}
