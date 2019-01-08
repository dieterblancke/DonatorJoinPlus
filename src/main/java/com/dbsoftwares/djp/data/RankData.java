package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import lombok.Data;
import com.dbsoftwares.djp.data.EventData.EventType;

import java.util.Map;

@Data
public class RankData {

    private String name;
    private int priority;
    private String permission;
    private Map<EventType, EventData> events;
    private boolean worldMessages;
    private Map<EventType, EventData> worldEvents;

    public void fromSection(final ISection section) {
        this.name = section.getString("name");
        this.priority = section.getInteger("priority");
        this.permission = section.getString("permission");

        final ISection worldSection = section.getSection("world");
        this.worldMessages = worldSection.getBoolean("enabled");

        for (EventType type : EventType.values()) {
            events.put(type, getData(type, section));
            worldEvents.put(type, getData(type, worldSection));
        }
    }

    private EventData getData(EventType type, ISection section) {
        final EventData data = new EventData(type);
        data.fromSection(section.getSection(type.toString().toLowerCase()));
        return data;
    }
}
