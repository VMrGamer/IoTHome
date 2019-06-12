package com.vedant.home;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.vedant.home.fragments.settings.SettingFragment;
import com.vedant.home.fragments.things.Contents;
import com.vedant.home.fragments.things.ThingFragment;
import com.vedant.home.helpers.AppData;
import com.vedant.home.helpers.PahoMQTTClient;
import com.vedant.home.ui.main.SectionsPagerAdapter;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ThingFragment.OnListFragmentInteractionListener, SettingFragment.OnListFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int NOTIFICATION_ID = 888;

    private NotificationManagerCompat notificationManagerCompat;
    private CoordinatorLayout mainCoordinatorLayout;
    private ViewPager viewPager;
    private MqttAndroidClient mqttAndroidClient;
    private PahoMQTTClient pahoMQTTClient;
    private String clientid = "";
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        clientid = "mqtt" + ((new Random()).nextInt(5000 - 1) + 1);
        AppData.CLIENT_ID = clientid;

        SharedPreferences sharedPref = getSharedPreferences(
                getApplication().getPackageName() + "MQTTPreferences",
                Context.MODE_PRIVATE);
        if(!sharedPref.contains("CLIENT_ID")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(AppData.CLIENT_ID_KEY, clientid);
            editor.putString(AppData.MQTT_BROKER_URL_KEY, "tcp://smarthomeec2.ml");
            editor.putString(AppData.MQTT_USERNAME_KEY, "ubuntu");
            editor.putString(AppData.MQTT_PASSWORD_KEY, "ubuntu");
            Set<String> strings = new ArraySet<>();
            ///editor.putString(AppData.ROOMS_KEY,)
            editor.putString(AppData.ROOM_ID_KEY, "001");
            strings = new ArraySet<>();
            strings.add("ipaddress");
            strings.add("light00");
            strings.add("light01");
            strings.add("light02");
            strings.add("light03");
            strings.add("light04");
            strings.add("light05");
            strings.add("light06");
            strings.add("light07");
            strings.add("fan00");
            strings.add("fan01");
            strings.add("fan03");
            editor.putStringSet(AppData.ROOM_ID,strings);
            editor.apply();
        }
        else {
            updateAppData();
        }

        pahoMQTTClient = new PahoMQTTClient();
        mqttAndroidClient = pahoMQTTClient.getMqttAndroidClient(
                getApplicationContext(),
                AppData.MQTT_BROKER_URL,
                AppData.CLIENT_ID,
                AppData.MQTT_USERNAME,
                AppData.MQTT_PASSWORD);

        mainCoordinatorLayout = findViewById(R.id.mainCoordinatorLayout);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        mqttCallback();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(!mqttAndroidClient.isConnected()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        subscriber();
                        break;
                    }
                }
            }
        },1000);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ScheduleTasks();
            }

        }, 0, 1000);
    }

    private void ScheduleTasks() {
        this.runOnUiThread(RunScheduledTasks);
    }

    private Runnable RunScheduledTasks = new Runnable() {
        public void run() {
            if(pahoMQTTClient.mqttAndroidClient.isConnected() ) {
                final TabLayout tabLayout = findViewById(R.id.tabs);
                ValueAnimator colorAnimation = ValueAnimator.ofArgb(
                        ((ColorDrawable)tabLayout.getBackground()).getColor(),
                        ContextCompat.getColor(getApplicationContext(),R.color.green)
                );
                colorAnimation.setDuration(300); // milliseconds
                colorAnimation.setInterpolator(new LinearInterpolator());
                colorAnimation.setRepeatCount(0);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        tabLayout.setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }
            else {
                final TabLayout tabLayout = findViewById(R.id.tabs);
                ValueAnimator colorAnimation = ValueAnimator.ofArgb(
                        ((ColorDrawable)tabLayout.getBackground()).getColor(),
                        ContextCompat.getColor(getApplicationContext(),R.color.red)
                );
                colorAnimation.setDuration(300); // milliseconds
                colorAnimation.setInterpolator(new LinearInterpolator());
                colorAnimation.setRepeatCount(0);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        tabLayout.setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }
        }
    };

    protected void mqttCallback() {
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                if(payload.equals("1")){
                    AppData.STATES.put(topic, true);
                } else if(payload.equals("0")){
                    AppData.STATES.put(topic, false);
                } else return;
                AppData.STATE_CHANGE.put(topic, true);
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public void onListFragmentInteraction(final Contents.Thing item) {
        if(!pahoMQTTClient.mqttAndroidClient.isConnected() ) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Toggle Switch");
            alertDialogBuilder.setMessage("Currently not connected to MQTT broker: Must be connected to publish message to a topic");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        } else { /*
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton("ON", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pahoMQTTClient.publishMessage(mqttAndroidClient, item.id, "1");
                }
            });
            alertDialogBuilder.setNegativeButton("OFF", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pahoMQTTClient.publishMessage(mqttAndroidClient, item.id, "0");
                }
            });*/
            if(item.state){
                AppData.STATES.put(Contents.nameToTopicMapper(item.content.replaceAll("[^A-Za-z0-9]", "")), false);
                pahoMQTTClient.publishMessage(mqttAndroidClient, item.id, "0");
            } else{
                AppData.STATES.put(Contents.nameToTopicMapper(item.content.replaceAll("[^A-Za-z0-9]", "")), true);
                pahoMQTTClient.publishMessage(mqttAndroidClient, item.id, "1");
            }
            AppData.STATE_CHANGE.put(Contents.nameToTopicMapper(item.content.replaceAll("[^A-Za-z0-9]", "")), true);
            viewPager.getAdapter().notifyDataSetChanged();
        }
        Toast.makeText(getApplicationContext(), item.content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListFragmentInteraction(final com.vedant.home.fragments.settings.Contents.Item item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.edit_setting_layout, null);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.newValue);
        alertDialogBuilder.setTitle("Edit Settings");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(getApplication().getPackageName() + "MQTTPreferences", Context.MODE_PRIVATE)
                                .edit()
                                .putString(item.id, userInput.getText().toString()).apply();
                        updateAppData();
                        viewPager.getAdapter().notifyDataSetChanged();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You Cancelled it", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Toast.makeText(getApplicationContext(), item.content, Toast.LENGTH_SHORT).show();
    }

    public void updateAppData(){
        SharedPreferences sharedPref = getSharedPreferences(getApplication().getPackageName() + "MQTTPreferences", Context.MODE_PRIVATE);
        AppData.CLIENT_ID = sharedPref.getString(AppData.CLIENT_ID_KEY,null);
        clientid = AppData.CLIENT_ID;
        AppData.MQTT_BROKER_URL = sharedPref.getString(AppData.MQTT_BROKER_URL_KEY,null);
        AppData.MQTT_USERNAME = sharedPref.getString(AppData.MQTT_USERNAME_KEY,null);
        AppData.MQTT_PASSWORD = sharedPref.getString(AppData.MQTT_PASSWORD_KEY,null);
        AppData.ROOM_ID = sharedPref.getString(AppData.ROOM_ID_KEY, null);
        AppData.APPLIANCES = sharedPref.getStringSet(AppData.ROOM_ID, new ArraySet<String>());
        AppData.ROOMS = sharedPref.getStringSet(AppData.ROOMS_KEY, new ArraySet<String>());
        for (String s : AppData.APPLIANCES){
            AppData.STATES.put(Contents.nameToTopicMapper(s.replaceAll("[^A-Za-z0-9]", "")),false);
        }
        for (String s : AppData.APPLIANCES){
            AppData.STATE_CHANGE.put(Contents.nameToTopicMapper(s.replaceAll("[^A-Za-z0-9]", "")),false);
        }
    }

    public void subscriber(){
        for(String s : AppData.APPLIANCES) {
            if(!s.equals("null")) {
                try {
                    pahoMQTTClient.subscribe(mqttAndroidClient, Contents.nameToTopicMapper(s), 1);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}