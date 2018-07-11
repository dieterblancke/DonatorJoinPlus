package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import lombok.Data;

import java.util.Map;

@Data
public class RankData {

    private String name;
    private int priority;
    private String permission;
    private EventData join;
    private EventData quit;

    @SuppressWarnings("unchecked")
    public void fromMap(Map map) {
        this.name = (String) map.get("name");
        this.priority = (int) map.get("priority");
        this.permission = (String) map.get("permission");

        this.join = getData(EventData.EventType.JOIN, map);
        this.quit = getData(EventData.EventType.QUIT, map);
    }

    private EventData getData(EventData.EventType type, Map map) {
        EventData data = new EventData(type);
        data.fromMap((Map) map.get(type.toString().toLowerCase()));
        return data;
    }
}
