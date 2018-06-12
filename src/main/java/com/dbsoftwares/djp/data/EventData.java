package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.djp.DonatorJoinPlus;
import lombok.Data;
import org.bukkit.Sound;
import java.util.Map;
import java.util.logging.Logger;

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
            try {
                this.sound = Sound.valueOf(sound.get("sound").toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                Logger logger = DonatorJoinPlus.getPlugin(DonatorJoinPlus.class).getLogger();

                logger.warning("The sound that was entered is invalid!");
                logger.warning(" Please use a sound of one of these pages:");
                logger.warning(" Version < 1.9: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html");
                logger.warning(" Version >= 1.9: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
                return;
            }
        }
        this.firework = (boolean) map.get("firework");
    }

    public enum EventType {
        JOIN, QUIT
    }
}