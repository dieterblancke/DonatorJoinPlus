package com.dbsoftwares.djp.utils;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;

public class Utils
{

    private Utils()
    {
    }

    public static String getMessage( final String path )
    {
        final ISection messages = DonatorJoinCore.getInstance().getMessages();

        final String message;
        if ( messages.isList( path ) )
        {
            message = String.join( "\n", messages.getStringList( path ) );
        }
        else
        {
            message = messages.getString( path );
        }

        return DonatorJoinCore.getInstance().color( getPrefix() + message );
    }

    public static String prefixedMessage( final String message )
    {
        return DonatorJoinCore.getInstance().color( getPrefix() + message );
    }

    public static String getPrefix()
    {
        return DonatorJoinCore.getInstance().color( DonatorJoinCore.getInstance().getMessages().getString( "prefix" ) );
    }
}