package com.dbsoftwares.djp.bungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BungeeUtils
{

    private static final Map<String, PlayerStorageData> STORAGE_DATA_MAP = new ConcurrentHashMap<>();
    private static final Pattern HEX_PATTERN = Pattern.compile( "<#([A-Fa-f0-9]){6}>" );

    public static void store( final ProxiedPlayer player, final PlayerStorageData storageData )
    {
        STORAGE_DATA_MAP.put( player.getUniqueId().toString(), storageData );
    }

    public static PlayerStorageData get( final ProxiedPlayer player )
    {
        return STORAGE_DATA_MAP.get( player.getUniqueId().toString() );
    }

    public static PlayerStorageData remove( final ProxiedPlayer player )
    {
        return STORAGE_DATA_MAP.remove( player.getUniqueId().toString() );
    }

    public static String c( String message )
    {
        Matcher matcher = HEX_PATTERN.matcher( message );
        while ( matcher.find() )
        {
            final ChatColor hexColor = ChatColor.of( matcher.group().substring( 1, matcher.group().length() - 1 ) );
            final String before = message.substring( 0, matcher.start() );
            final String after = message.substring( matcher.end() );

            message = before + hexColor + after;
            matcher = HEX_PATTERN.matcher( message );
        }
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
