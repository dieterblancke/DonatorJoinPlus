package dev.endoy.djp.spigot.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import dev.endoy.configuration.api.ISection;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RankData
{

    private String name;
    private int priority;
    private String permission;
    private Map<EventData.EventType, EventData> events = new HashMap<>();
    private Map<EventData.EventType, EventData> worldEvents = new HashMap<>();

    public void fromSection( final ISection section )
    {
        this.name = section.getString( "name" );
        this.priority = section.getInteger( "priority" );
        this.permission = section.getString( "permission" );

        final ISection worldSection = section.exists( "world" ) ? section.getSection( "world" ) : null;

        for ( EventData.EventType type : EventData.EventType.values() )
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

    private EventData getData( final EventData.EventType type, final ISection section, final boolean world )
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
