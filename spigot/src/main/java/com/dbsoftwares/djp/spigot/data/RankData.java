package com.dbsoftwares.djp.spigot.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.spigot.data.EventData.EventType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RankData
{

    private String name;
    private int priority;
    private String permission;
    private Map<EventType, EventData> events = new HashMap<>();
    private Map<EventType, EventData> worldEvents = new HashMap<>();

    public void fromSection( final ISection section )
    {
        this.name = section.getString( "name" );
        this.priority = section.getInteger( "priority" );
        this.permission = section.getString( "permission" );

        final ISection worldSection = section.exists( "world" ) ? section.getSection( "world" ) : null;

        for ( EventType type : EventType.values() )
        {
            final EventData eventData = getData( type, section );

            if ( eventData != null )
            {
                events.put( type, eventData );
            }

            if ( worldSection != null )
            {
                final EventData worldEventData = getData( type, worldSection );

                if ( worldEventData != null )
                {
                    worldEvents.put( type, worldEventData );
                }
            }
        }
    }

    private EventData getData( EventType type, ISection section )
    {
        final String path = type.toString().toLowerCase();
        section = section.exists( path ) ? section.getSection( path ) : null;

        if ( section == null )
        {
            return null;
        }

        final EventData data = new EventData( type );
        data.fromSection( section );
        return data;
    }
}
