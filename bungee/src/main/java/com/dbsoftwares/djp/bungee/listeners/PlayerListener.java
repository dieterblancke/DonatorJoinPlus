package com.dbsoftwares.djp.bungee.listeners;

import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.bungee.DonatorJoinPlus;
import com.dbsoftwares.djp.bungee.data.EventData;
import com.dbsoftwares.djp.bungee.data.RankData;
import com.dbsoftwares.djp.bungee.utils.BungeeUtils;
import com.dbsoftwares.djp.bungee.utils.MessageBuilder;
import com.dbsoftwares.djp.bungee.utils.PlayerStorageData;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayerListener extends DJPListener implements Listener
{

    private final Cache<UUID, CompletableFuture<PlayerStorageData>> loadingCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 15, TimeUnit.SECONDS )
            .build();

    public PlayerListener()
    {
        // Cleanup task, runs every 3 minutes to ensure the PlayerListener loadingCache is cleaned up.
        ProxyServer.getInstance().getScheduler().schedule( DonatorJoinPlus.i(), () ->
        {
            if ( DonatorJoinPlus.i().isDebugMode() )
            {
                DonatorJoinPlus.i().getLogger().info( String.format( "Cleaning up loading cache ... [initialSize=%s]", loadingCache.size() ) );
            }

            loadingCache.cleanUp();

            if ( DonatorJoinPlus.i().isDebugMode() )
            {
                DonatorJoinPlus.i().getLogger().info( String.format( "Successfully up loading cache ... [currentSize=%s]", loadingCache.size() ) );
            }
        }, 3, 3, TimeUnit.MINUTES );
    }

    @EventHandler
    public void onLogin( LoginEvent event )
    {
        final String name = event.getConnection().getName();
        final UUID uuid = event.getConnection().getUniqueId();

        DonatorJoinPlus.i().debug( "Initializing loading of storage for player " + name + " and uuid " + uuid.toString() + "." );

        final CompletableFuture<PlayerStorageData> future = CompletableFuture.supplyAsync( () ->
        {
            final AbstractStorageManager storage = DonatorJoinCore.getInstance().getStorage();

            return new PlayerStorageData(
                    uuid,
                    storage.exists( uuid ),
                    storage.isToggled( uuid ),
                    true
            );
        } );
        loadingCache.put( uuid, future );
    }

    @EventHandler
    public void onPostLogin( PostLoginEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final CompletableFuture<PlayerStorageData> completableFuture = loadingCache.getIfPresent( player.getUniqueId() );
        try
        {
            final PlayerStorageData storageData = completableFuture.get();

            if ( !storageData.isExists() )
            {
                handleFirstJoin( player );
            }
            if ( !BungeeUtils.isVanished( player ) && !storageData.isToggled() )
            {
                execute( player, EventData.EventType.JOIN );
            }

            BungeeUtils.store( player, storageData );
            CompletableFuture.runAsync( () -> DonatorJoinCore.getInstance().getStorage().toggle( player.getUniqueId(), false ) );
        }
        catch ( NullPointerException | InterruptedException | ExecutionException e )
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onDisconnect( PlayerDisconnectEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final PlayerStorageData storageData = BungeeUtils.remove( player );

        if ( !BungeeUtils.isVanished( player ) && !storageData.isToggled() )
        {
            execute( player, EventData.EventType.QUIT );
        }
    }

    @EventHandler
    public void onSwitch( ServerSwitchEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final PlayerStorageData storageData = BungeeUtils.get( player );

        if ( storageData.isNetworkJoin() )
        {
            storageData.setNetworkJoin( false );
            return;
        }
        if ( !BungeeUtils.isVanished( player ) && !storageData.isToggled() )
        {
            execute( player, EventData.EventType.SWITCH );
        }
    }

    private void handleFirstJoin( ProxiedPlayer player )
    {
        if ( !DonatorJoinPlus.i().getConfiguration().getBoolean( "firstjoin.enabled", false ) )
        {
            return;
        }
        for ( String message : DonatorJoinPlus.i().getConfiguration().getStringList( "firstjoin.message" ) )
        {
            ProxyServer.getInstance().broadcast( TextComponent.fromLegacyText( BungeeUtils.formatString( player, message ) ) );
        }
    }
}
