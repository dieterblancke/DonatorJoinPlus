package dev.endoy.djp.bungee.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import dev.endoy.configuration.api.ISection;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
public class EventData
{

    private static final Random RANDOM = new Random();

    private EventType type;
    private boolean enabled;
    private Object message;
    private boolean commandsEnabled;
    private List<String> commands;

    public EventData( final EventType type )
    {
        this.type = type;
    }

    public void fromSection( final ISection section )
    {
        this.enabled = section.getBoolean( "enabled" );
        if ( section.isList( "message" ) )
        {
            message = section.getList( "message" );
        }
        else if ( section.isSection( "message" ) )
        {
            message = section.getSection( "message" );
        }
        else
        {
            message = section.getString( "message" );
        }

        if ( section.exists( "commands" ) )
        {
            final ISection commands = section.getSection( "commands" );

            this.commandsEnabled = commands.getBoolean( "enabled" );
            this.commands = commands.getStringList( "commands" );
        }
    }

    public Object getMessage()
    {
        if ( message instanceof List )
        {
            return ( (List<?>) message ).get( RANDOM.nextInt( ( (List<?>) message ).size() ) );
        }
        else
        {
            return message;
        }
    }

    public enum EventType
    {
        JOIN, QUIT, SWITCH
    }
}