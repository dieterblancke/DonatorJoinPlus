package com.dbsoftwares.djp.spigot.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import be.dieterblancke.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.spigot.utils.XSound;
import lombok.Data;
import org.bukkit.Sound;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Data
public class EventData
{

    private static final Random RANDOM = new Random();

    private EventType type;
    private boolean enabled;
    private Object message;
    private boolean soundEnabled;
    private Sound sound;
    private float soundPitch = -20F;
    private float soundVolume = 20F;
    private boolean firework;
    private boolean commandsEnabled;
    private long delay;
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

        this.delay = section.exists( "delay" ) ? section.getLong( "delay" ) : 0;

        final ISection sound = section.getSection( "sound" );
        this.soundEnabled = sound.getBoolean( "enabled" );
        if ( soundEnabled )
        {
            try
            {
                this.sound = XSound.matchXSound( sound.getString( "sound" ).toUpperCase() )
                        .orElseThrow( IllegalAccessError::new )
                        .parseSound();
            }
            catch ( IllegalArgumentException e )
            {
                final Logger logger = DonatorJoinCore.getInstance().getLogger();

                logger.warning( "The sound that was entered is invalid!" );
                logger.warning( "For all available sounds, please check out: https://github.com/dieterblancke/DonatorJoinPlus/blob/master/spigot/src/main/java/com/dbsoftwares/djp/spigot/utils/XSound.java#L70-L883" );
                logger.warning( "You could also use /djp listsounds to get a list of sounds available in your current version." );
                return;
            }

            if ( sound.exists( "pitch" ) )
            {
                this.soundPitch = sound.getFloat( "pitch" );
            }
            if ( sound.exists( "volume" ) )
            {
                this.soundVolume = sound.getFloat( "volume" );
            }
        }
        this.firework = section.getBoolean( "firework" );

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
        JOIN, QUIT
    }
}