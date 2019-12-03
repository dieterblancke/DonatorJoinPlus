package com.dbsoftwares.djp.spigot.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;
import lombok.Data;
import org.bukkit.Sound;
import org.slf4j.Logger;

import java.util.List;

@Data
public class EventData
{

    private EventType type;
    private boolean enabled;
    private String message;
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
        this.message = section.getString( "message" );

        final ISection sound = section.getSection( "sound" );
        this.soundEnabled = sound.getBoolean( "enabled" );
        if ( soundEnabled )
        {
            try
            {
                this.sound = Sound.valueOf( sound.getString( "sound" ).toUpperCase() );
            }
            catch ( IllegalArgumentException e )
            {
                final Logger logger = DonatorJoinCore.getInstance().getLog();

                logger.warn( "The sound that was entered is invalid!" );
                logger.warn( "Please use a sound of one of these pages:" );
                logger.warn( "- Version < 1.9: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html" );
                logger.warn( "- Version 1.9 - 1.12.2: http://bit.ly/2RuwTrj" );
                logger.warn( "- Newest version: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html" );
                logger.warn( "You could also use /djp listsounds to get a list of sounds available in your current version." );
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

    public enum EventType
    {
        JOIN, QUIT
    }
}