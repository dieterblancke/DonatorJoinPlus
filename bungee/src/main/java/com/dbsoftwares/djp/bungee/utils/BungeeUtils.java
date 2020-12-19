package com.dbsoftwares.djp.bungee.utils;

import net.md_5.bungee.api.ChatColor;
import com.dbsoftwares.djp.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicInteger;

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
        return STORAGE_DATA_MAP.getOrDefault( player.getUniqueId().toString(), new PlayerStorageData( player.getUniqueId() ) );
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

    public static String formatString( final ProxiedPlayer p, String str )
    {
        str = str.replace( "%player%", p.getName() );
        str = str.replace( "{player}", p.getName() );

        if ( p.getServer() != null && p.getServer().getInfo() != null )
        {
            str = str.replace( "%server%", p.getServer().getInfo().getName() );
            str = str.replace( "{server}", p.getServer().getInfo().getName() );
        }
        str = c( str );

        if ( ProxyServer.getInstance().getPluginManager().getPlugin( "BungeeUtilisalsX" ) != null )
        {
            // formatting message with BungeeUtilisalsX placeholders :^)
            str = com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI.formatMessage(
                    com.dbsoftwares.bungeeutilisals.api.BUCore.getApi().getUser( p ).orElse( null ),
                    str
            );
        }
        return str;
    }

    public static BaseComponent[] format( final ProxiedPlayer player, final List<String> messages )
    {
        final AtomicInteger count = new AtomicInteger();
        return messages
                .stream()
                .map( message ->
                {
                    if ( count.incrementAndGet() >= messages.size() )
                    {
                        return c( formatString( player, message ) );
                    }
                    return c( formatString( player, message + "\n" ) );
                } )
                .map( message -> new BaseComponent[]{ new TextComponent( message ) } )
                .flatMap( Arrays::stream )
                .toArray( BaseComponent[]::new );
    }
}
