package com.dbsoftwares.djp.bungee.utils;

import com.dbsoftwares.djp.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BungeeUtils
{

    private static final Map<String, PlayerStorageData> STORAGE_DATA_MAP = new ConcurrentHashMap<>();

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

    public static String formatString( final ProxiedPlayer p, String str )
    {
        str = str.replace( "%player%", p.getName() );
        str = str.replace( "{player}", p.getName() );

        if ( p.getServer() != null && p.getServer().getInfo() != null )
        {
            str = str.replace( "%server%", p.getServer().getInfo().getName() );
            str = str.replace( "{server}", p.getServer().getInfo().getName() );
        }
        str = Utils.c( str );

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
                        return Utils.c( formatString( player, message ) );
                    }
                    return Utils.c( formatString( player, message + "\n" ) );
                } )
                .map( message -> new BaseComponent[]{ new TextComponent( message ) } )
                .flatMap( Arrays::stream )
                .toArray( BaseComponent[]::new );
    }
}
