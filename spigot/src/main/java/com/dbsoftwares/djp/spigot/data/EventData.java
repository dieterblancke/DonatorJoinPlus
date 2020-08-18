package com.dbsoftwares.djp.spigot.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.spigot.utils.XSound;
import com.google.common.collect.Lists;
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
    private List<String> messages;
    private boolean soundEnabled;
    private Sound sound;
    private boolean firework;
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
        }
        this.firework = section.getBoolean( "firework" );

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
        JOIN, QUIT
    }
}