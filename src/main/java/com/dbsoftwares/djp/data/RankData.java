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
    private EventData join;
    private EventData quit;

    public void fromMap(Map map) {
        this.name = (String) map.get(name);

        this.join = getData(EventData.EventType.JOIN, map);
        this.quit = getData(EventData.EventType.QUIT, map);
    }

    private EventData getData(EventData.EventType type, Map map) {
        EventData data = new EventData(type);
        data.fromMap((Map) map.get(type.toString().toLowerCase()));
        return data;
    }
}
