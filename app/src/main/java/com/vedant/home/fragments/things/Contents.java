package com.vedant.home.fragments.things;

import com.vedant.home.helpers.AppData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contents {

    public static List<Thing> THINGS = new ArrayList<>();
    public static Map<String, Thing> THING_MAP = new HashMap<>();

    public static void addThing(Thing item) {
        THINGS.add(item);
        THING_MAP.put(item.id, item);
    }

    public Contents(){
        THING_MAP = new HashMap<>();
        THINGS = new ArrayList<>();
    }

    public static String nameToTopicMapper(String name){
        return "mumbai01/"+AppData.ROOM_ID + "/" + name;
    }

    public static class Thing {
        public final String id;
        public final String content;
        public final String details;
        public final Boolean state_change;
        public Boolean state;
        public final String topic;

        public Thing(String id, String content, String details, Boolean state, Boolean state_change, String topic) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.state = state;
            this.state_change = state_change;
            this.topic = topic;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
