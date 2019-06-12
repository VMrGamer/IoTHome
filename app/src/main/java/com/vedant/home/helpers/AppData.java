package com.vedant.home.helpers;

import android.util.ArraySet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppData {
    public static String MQTT_BROKER_URL = "tcp://smarthomeec2.ml";
    public static String MQTT_USERNAME = "ubuntu";
    public static String MQTT_PASSWORD = "ubuntu";
    public static String CLIENT_ID = "just-a-placeholder";

    public static String ROOM_ID = "001";

    public static Set<String> APPLIANCES = new ArraySet<>();
    public static Map<String,Boolean> STATES = new HashMap<>();
    public static Map<String,Boolean> STATE_CHANGE = new HashMap<>();
    public static Set<String> ROOMS = new ArraySet<>();

    public static final String CONNECTED = "Connected";
    public static final String NOTCONNECTED = "Not Connected";

    public static final String MQTT_BROKER_URL_KEY = "MQTT_BROKER_URL";
    public static final String MQTT_USERNAME_KEY = "MQTT_USERNAME";
    public static final String MQTT_PASSWORD_KEY = "MQTT_PASSWORD";
    public static final String CLIENT_ID_KEY = "CLIENT_ID";

    public static final String ROOM_ID_KEY = "ROOM_ID";
    public static final String ROOMS_KEY = "ROOMS";
}
