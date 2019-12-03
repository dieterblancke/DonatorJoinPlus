package com.dbsoftwares.djp.bungee.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
}
