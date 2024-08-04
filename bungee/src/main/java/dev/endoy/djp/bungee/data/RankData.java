package dev.endoy.djp.bungee.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import dev.endoy.configuration.api.ISection;
import dev.endoy.djp.bungee.data.EventData.EventType;
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

    public void fromSection( final ISection section )
    {
        this.name = section.getString( "name" );
        this.priority = section.getInteger( "priority" );
        this.permission = section.getString( "permission" );

        for ( EventType type : EventType.values() )
        {
            final EventData eventData = getData( type, section );

            if ( eventData != null )
            {
                events.put( type, eventData );
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
