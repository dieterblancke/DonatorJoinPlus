package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import lombok.Data;
import com.dbsoftwares.djp.data.EventData.EventType;

@Data
public class RankData {

    private String name;
    private int priority;
    private String permission;
    private EventData join;
    private EventData quit;

    public void fromSection(final ISection section) {
        this.name = section.getString("name");
        this.priority = section.getInteger("priority");
        this.permission = section.getString("permission");

        this.join = getData(EventType.JOIN, section);
        this.quit = getData(EventType.QUIT, section);
    }

    private EventData getData(EventType type, ISection section) {
        final EventData data = new EventData(type);
        data.fromSection(section.getSection(type.toString().toLowerCase()));
        return data;
    }
}
