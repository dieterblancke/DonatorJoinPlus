package com.dbsoftwares.djp.spigot.data;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode( callSuper = true )
public class WorldEventData extends EventData
{

    private boolean shouldFilterWorlds = false;
    private boolean whitelistMode;
    private List<String> worldNames;

    public WorldEventData( final EventType type )
    {
        super( type );
    }

    public void fromSection( final ISection worldSection, final ISection section )
    {
        super.fromSection( section );

        if ( worldSection.exists( "worlds" ) )
        {
            final ISection worlds = worldSection.getSection( "worlds" );

            this.shouldFilterWorlds = true;
            this.whitelistMode = worlds.getString( "mode" ).equalsIgnoreCase( "whitelist" );
            this.worldNames = worlds.getStringList( "list" );
        }
    }

    public boolean ckeckWorld( final String world )
    {
        if ( !shouldFilterWorlds )
        {
            return false;
        }
        if ( whitelistMode )
        {
            for ( String worldName : worldNames )
            {
                if ( world.equalsIgnoreCase( worldName ) )
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            for ( String worldName : worldNames )
            {
                if ( world.equalsIgnoreCase( worldName ) )
                {
                    return true;
                }
            }
            return false;
        }
    }
}