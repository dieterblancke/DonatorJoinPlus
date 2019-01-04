package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinPlus;
import lombok.Data;
import org.bukkit.Sound;
import org.slf4j.Logger;

import java.util.Map;

@Data
public class EventData {

    private EventType type;
    private boolean enabled;
    private String message;
    private boolean soundEnabled;
    private Sound sound;
    private boolean firework;

    public EventData(final EventType type) {
        this.type = type;
    }

    public void fromSection(final ISection section) {
        this.enabled = section.getBoolean("enabled");
        this.message = section.getString("message");

        final ISection sound = section.getSection("sound");
        this.soundEnabled = sound.getBoolean("enabled");
        if (soundEnabled) {
            try {
                this.sound = Sound.valueOf(sound.getString("sound").toUpperCase());
            } catch (IllegalArgumentException e) {
                final Logger logger = DonatorJoinPlus.getLog();

                logger.warn("The sound that was entered is invalid!");
                logger.warn("Please use a sound of one of these pages:");
                logger.warn("- Version < 1.9: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html");
                logger.warn("- Version 1.9 - 1.12.2: http://bit.ly/2RuwTrj");
                logger.warn("- Newest version: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
                return;
            }
        }
        this.firework = section.getBoolean("firework");
    }

    public enum EventType {
        JOIN, QUIT
    }
}