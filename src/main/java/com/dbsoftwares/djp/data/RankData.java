package com.dbsoftwares.djp.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.data.EventData.EventType;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class RankData
{

    private String name;
    private int priority;
    private String permission;
    private Map<EventType, EventData> events = Maps.newHashMap();
    private Map<EventType, EventData> worldEvents = Maps.newHashMap();

    public void fromSection( final ISection section )
    {
        this.name = section.getString( "name" );
        this.priority = section.getInteger( "priority" );
        this.permission = section.getString( "permission" );

        final ISection worldSection = section.exists( "world" ) ? section.getSection( "world" ) : null;

        for ( EventType type : EventType.values() )
        {
            final EventData eventData = getData( type, section, false );

            if ( eventData != null )
            {
                events.put( type, eventData );
            }

            if ( worldSection != null )
            {
                final EventData worldEventData = getData( type, worldSection, true );

                if ( worldEventData != null )
                {
                    worldEvents.put( type, worldEventData );
                }
            }
        }
    }

    private EventData getData( final EventType type, final ISection section, final boolean world )
    {
        final String path = type.toString().toLowerCase();
        final ISection typeSection = section.exists( path ) ? section.getSection( path ) : null;

        if ( typeSection == null )
        {
            return null;
        }

        if ( world )
        {
            final WorldEventData worldEventData = new WorldEventData( type );

            worldEventData.fromSection( section, typeSection );
            return worldEventData;
        }
        else
        {
            final EventData eventData = new EventData( type );

            eventData.fromSection( typeSection );
            return eventData;
        }
    }
}
