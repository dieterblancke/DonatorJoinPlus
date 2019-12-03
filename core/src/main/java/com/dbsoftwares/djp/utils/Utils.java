package com.dbsoftwares.djp.utils;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.djp.DonatorJoinCore;

public class Utils
{

    private Utils()
    {
    }

    public static String c( String message )
    {
        // not recommended, but doing it anyways :D
        return message.replace( "&", "§" );
    }

    public static String getMessage( final String path )
    {
        final IConfiguration config = DonatorJoinCore.getInstance().getConfiguration();

        final String message;
        if ( config.isList( "messages." + path ) )
        {
            message = String.join( "\n", config.getStringList( "messages." + path ) );
        }
        else
        {
            message = config.getString( "messages." + path );
        }

        return c( getPrefix() + message );
    }

    public static String prefixedMessage( final String message )
    {
        return c( getPrefix() + message );
    }

    public static String getPrefix()
    {
        return c( DonatorJoinCore.getInstance().getConfiguration().getString( "messages.prefix" ) );
    }
}