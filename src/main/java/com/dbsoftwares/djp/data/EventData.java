package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import lombok.Data;
import org.bukkit.Sound;

import java.util.Map;

@Data
public class EventData {

    private EventType type;
    private boolean enabled;
    private String message;
    private boolean soundEnabled;
    private Sound sound;
    private boolean firework;

    public EventData(EventType type) {
        this.type = type;
    }

    public void fromMap(Map map) {
        this.enabled = (boolean) map.get("enabled");
        this.message = (String) map.get("message");

        Map sound = (Map) map.get("sound");
        this.soundEnabled = (boolean) sound.get("enabled");
        if (soundEnabled) {
            this.sound = Sound.valueOf(sound.get("sound").toString().toUpperCase());
        }
        this.firework = (boolean) map.get("firework");
    }

    public enum EventType {
        JOIN, QUIT
    }
}