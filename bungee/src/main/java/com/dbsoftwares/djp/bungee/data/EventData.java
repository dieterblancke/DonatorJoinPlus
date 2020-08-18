package com.dbsoftwares.djp.bungee.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
public class EventData
{

    private static final Random RANDOM = new Random();

    private EventType type;
    private boolean enabled;
    private List<String> messages;
    private boolean commandsEnabled;
    private List<String> commands;

    public EventData( final EventType type )
    {
        this.type = type;
    }

    public void fromSection( final ISection section )
    {
        this.enabled = section.getBoolean( "enabled" );
        this.messages = section.isList( "message" ) ? section.getStringList( "message" ) : Lists.newArrayList( section.getString( "message" ) );

        if ( section.exists( "commands" ) )
        {
            final ISection commands = section.getSection( "commands" );

            this.commandsEnabled = commands.getBoolean( "enabled" );
            this.commands = commands.getStringList( "commands" );
        }
    }

    public String getMessage()
    {
        if ( messages.isEmpty() )
        {
            return "";
        }

        return messages.size() == 1 ? messages.get( 0 ) : messages.get( RANDOM.nextInt( messages.size() ) );
    }

    public enum EventType
    {
        JOIN, QUIT, SWITCH
    }
}