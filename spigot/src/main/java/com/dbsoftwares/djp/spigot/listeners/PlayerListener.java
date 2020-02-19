package com.dbsoftwares.djp.spigot.listeners;

import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import com.dbsoftwares.djp.spigot.data.EventData;
import com.dbsoftwares.djp.spigot.data.EventData.EventType;
import com.dbsoftwares.djp.spigot.data.RankData;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.spigot.utils.XSound;
import com.dbsoftwares.djp.user.User;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

public class PlayerListener implements Listener
{

    private final Cache<UUID, CompletableFuture<User>> loadingCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 15, TimeUnit.SECONDS )
            .build();

    public PlayerListener()
    {
        // Cleanup task, runs every 3 minutes to ensure the PlayerListener loadingCache is cleaned up.
        new BukkitRunnable()
        {
            @Override
            public void run()
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
            }
        }.runTaskTimerAsynchronously( DonatorJoinPlus.i(), 3600, 3600 );
    }

    @EventHandler
    public void onFirstJoin( final PlayerJoinEvent event )
    {
        if ( !DonatorJoinPlus.i().getConfiguration().getBoolean( "firstjoin.enabled", false ) )
        {
            return;
        }

        final Player player = event.getPlayer();
        if ( !player.hasPlayedBefore() )
        {
            for ( String message : DonatorJoinPlus.i().getConfiguration().getStringList( "firstjoin.message" ) )
            {
                Bukkit.broadcastMessage( Utils.c( message.replace( "{player}", player.getName() ) ) );
            }
        }
    }

    @EventHandler
    public void onLoad( final PlayerLoginEvent event )
    {
        final Player player = event.getPlayer();

        DonatorJoinPlus.i().debug( "Initializing loading of storage for player " + player.getName() + "." );

        final CompletableFuture<User> future = CompletableFuture.supplyAsync( () -> DonatorJoinPlus.i().getStorage().getUser( player.getUniqueId() ) );
        loadingCache.put( player.getUniqueId(), future );
    }

    @EventHandler
    public void onJoin( final PlayerJoinEvent event )
    {
        final Player p = event.getPlayer();

        final User user = getUser( p.getUniqueId() );
        SpigotUtils.setMetaData( p, SpigotUtils.USER_KEY, user );

        if ( !DonatorJoinPlus.i().isDisableJoinMessage() )
        {
            event.setJoinMessage( null );
        }

        if ( SpigotUtils.isVanished( p ) || user.isToggled() )
        {
            return;
        }

        DonatorJoinPlus.i().debug( "Executing login event for player " + p.getName() + "." );

        executeEvent( user, true, null, p );
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event )
    {
        if ( !DonatorJoinPlus.i().isDisableQuitMessage() )
        {
            event.setQuitMessage( null );
        }

        final Player p = event.getPlayer();
        final User user = (User) SpigotUtils.getMetaData( p, SpigotUtils.USER_KEY, null );
        final boolean toggled = user != null && user.isToggled();

        if ( SpigotUtils.isVanished( p ) || toggled )
        {
            return;
        }
        executeEvent( user, false, null, p );
        DonatorJoinPlus.i().debug( "Executing logout event for player " + p.getName() + "." );
    }

    @EventHandler
    public void onWorldChange( PlayerChangedWorldEvent event )
    {
        final Player p = event.getPlayer();
        final User user = (User) SpigotUtils.getMetaData( p, SpigotUtils.USER_KEY, null );
        final boolean toggled = user != null && user.isToggled();

        if ( SpigotUtils.isVanished( p ) || toggled )
        {
            return;
        }
        executeEvent( user, false, event.getFrom(), p );
        executeEvent( user, true, event.getPlayer().getWorld(), p );
    }

    private User getUser( final UUID uuid )
    {
        try
        {
            final CompletableFuture<User> future = loadingCache.getIfPresent( uuid );

            if ( future == null )
            {
                return DonatorJoinPlus.i().getStorage().getUser( uuid );
            }
            return future.get();
        }
        catch ( InterruptedException | ExecutionException e )
        {
            return DonatorJoinPlus.i().getStorage().getUser( uuid );
        }
    }

    private void executeEvent( final User user, final boolean join, final World world, final Player p )
    {
        final String[] groups = DonatorJoinPlus.i().getPermission().getPlayerGroups( p );

        DonatorJoinPlus.i().debug( "List of groups for player " + p.getName() + ": " + Arrays.toString( groups ) );

        for ( RankData data : DonatorJoinPlus.i().getRankData() )
        {
            final EventType type = join ? EventType.JOIN : EventType.QUIT;
            final EventData eventData = (world != null ? data.getWorldEvents() : data.getEvents()).getOrDefault( type, null );

            if ( eventData == null )
            {
                continue;
            }

            if ( DonatorJoinPlus.i().isUsePermissions() )
            {
                if ( DonatorJoinPlus.i().getPermission().has( p, data.getPermission() ) )
                {
                    DonatorJoinPlus.i().debug( "Player " + p.getName() + " has the permission " + data.getPermission() + ", executing event ..." );

                    executeEventData( user, p, eventData, world );

                    if ( DonatorJoinPlus.i().getConfiguration().getBoolean( "usepriorities" ) )
                    {
                        break;
                    }
                }
                else
                {
                    DonatorJoinPlus.i().debug( "Player " + p.getName() + " does not have the permission " + data.getPermission() + "." );
                }
            }
            else
            {
                if ( SpigotUtils.contains( groups, data.getName() ) )
                {
                    DonatorJoinPlus.i().debug( "Player " + p.getName() + " is in the group " + data.getName() + ", executing event ..." );
                    executeEventData( user, p, eventData, world );

                    if ( DonatorJoinPlus.i().getConfiguration().getBoolean( "usepriorities" ) )
                    {
                        break;
                    }
                }
            }
        }
    }

    private void executeEventData( final User user, final Player p, final EventData eventData, final World world )
    {
        if ( eventData.isEnabled() )
        {
            final String message = formatString( p, eventData.getMessage() );

            if ( !message.isEmpty() )
            {
                for ( String msg : message.split( "<nl>" ) )
                {
                    if ( world != null )
                    {
                        for ( Player player : world.getPlayers() )
                        {
                            player.sendMessage( msg );
                        }
                        Bukkit.getConsoleSender().sendMessage( msg );
                    }
                    else
                    {
                        Bukkit.broadcastMessage( msg );
                    }
                }
            }

            if ( eventData.isFirework() && !user.isFireworkToggled() )
            {
                SpigotUtils.spawnFirework( p.getLocation() );
            }

            if ( eventData.isSoundEnabled() && !user.isSoundToggled() )
            {
                final String soundName = eventData.getType() == EventType.JOIN ? user.getJoinSound() : user.getLeaveSound();

                if ( soundName != null && XSound.contains( soundName ) )
                {
                    final XSound sound = XSound.matchXSound( soundName ).orElse( null );

                    sound.playSound( p, 20F, -20F );
                }
                else if ( eventData.getSound() != null )
                {
                    final XSound sound = XSound.matchXSound( eventData.getSound() );

                    sound.playSound( p, 20F, -20F );
                }
            }

            if ( eventData.isCommandsEnabled() && eventData.getCommands() != null && !eventData.getCommands().isEmpty() )
            {
                for ( String command : eventData.getCommands() )
                {
                    command = formatString( p, command );

                    DonatorJoinPlus.i().debug( "Executing command " + command + " for player " + p.getName() + "." );

                    if ( command.startsWith( "player:" ) )
                    {
                        p.performCommand( command.replaceFirst( "player:", "" ) );
                    }
                    else
                    {
                        Bukkit.dispatchCommand( Bukkit.getConsoleSender(), command );
                    }
                }
            }
        }
    }

    private String formatString( final Player p, String str )
    {
        if ( str == null || str.isEmpty() )
        {
            return "";
        }
        str = str.replace( "%player%", p.getName() );
        str = str.replace( "{player}", p.getName() );
        str = Utils.c( str );

        if ( Bukkit.getPluginManager().isPluginEnabled( "PlaceholderAPI" ) )
        {
            str = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders( (OfflinePlayer) p, str );
        }
        return str;
    }
}