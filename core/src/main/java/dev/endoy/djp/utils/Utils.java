package dev.endoy.djp.utils;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import dev.endoy.configuration.api.ISection;
import dev.endoy.djp.DonatorJoinCore;

import java.util.function.Function;

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

    public static int getJavaVersion()
    {
        String version = System.getProperty( "java.version" );

        if ( version.startsWith( "1." ) )
        {
            version = version.substring( 2, 3 );
        }
        else
        {
            int dot = version.indexOf( "." );
            if ( dot != -1 )
            {
                version = version.substring( 0, dot );
            }
        }
        return Integer.parseInt( version );
    }

    public static <T> T getFromArrayOrDefault( T[] array, int index, T def )
    {
        if ( index >= array.length )
        {
            return def;
        }
        return array[index];
    }

    public static <T, V> V getFromArrayOrDefault( T[] array, int index, V def, Function<T, V> mapper )
    {
        if ( index >= array.length )
        {
            return def;
        }
        return mapper.apply( array[index] );
    }
}