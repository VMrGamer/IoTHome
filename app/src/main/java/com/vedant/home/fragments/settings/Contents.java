package com.vedant.home.fragments.settings;

import android.support.annotation.NonNull;

import com.vedant.home.helpers.AppData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contents {

    public static List<Item> ITEMS = new ArrayList<>();

    public static Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

    static {
        addItem(new Item(AppData.CLIENT_ID_KEY,"Client ID", "Current Value: "+ AppData.CLIENT_ID +".Tap to edit"));
        addItem(new Item(AppData.MQTT_BROKER_URL_KEY,"Broker URL", "Current Value: "+ AppData.MQTT_BROKER_URL +".Tap to edit"));
        addItem(new Item(AppData.MQTT_USERNAME_KEY,"Username", "Current Value: "+ AppData.MQTT_USERNAME +".Tap to edit"));
        addItem(new Item(AppData.MQTT_PASSWORD_KEY,"Password", "Current Value: "+ AppData.MQTT_PASSWORD +".Tap to edit"));
    }

    public static void addItem(Item item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class Item {
        public final String id;
        public final String content;
        public final String details;

        public Item(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @NonNull
        @Override
        public String toString() {
            return content;
        }
    }
}
