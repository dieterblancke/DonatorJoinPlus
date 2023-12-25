package dev.endoy.djp.spigot.listeners;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.DonatorJoinEventHelper;
import dev.endoy.djp.spigot.utils.MessageBuilder;
import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.user.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
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
                DonatorJoinEventHelper.broadcast( MessageBuilder.buildMessage( player, message.replace( "{player}", player.getName() ) ) );
            }
        }
    }

    @EventHandler
    public void onLoad( PlayerLoginEvent event )
    {
        Player player = event.getPlayer();

        DonatorJoinPlus.i().debug( "Initializing loading of storage for player " + player.getName() + "." );

        loadingCache.put( player.getUniqueId(), CompletableFuture.supplyAsync( () -> DonatorJoinPlus.i().getStorage().getUser( player.getUniqueId() ) ) );
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event )
    {
        Player p = event.getPlayer();
        User user = getUser( p.getUniqueId() );

        SpigotUtils.setMetaData( p, SpigotUtils.USER_KEY, user );

        DonatorJoinPlus.i().debug( "User loaded for " + p.getName() + ": " + user.toString() );

        if ( !DonatorJoinPlus.i().isDisableJoinMessage() )
        {
            event.setJoinMessage( null );
        }

        if ( DonatorJoinPlus.i().getVanishIntegration().isVanished( p ) || user.isToggled() )
        {
            return;
        }

        DonatorJoinPlus.i().debug( "Executing login event for player " + p.getName() + "." );
        DonatorJoinEventHelper.executeEvent( user, true, null, p );
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event )
    {
        if ( !DonatorJoinPlus.i().isDisableQuitMessage() )
        {
            event.setQuitMessage( null );
        }

        final Player p = event.getPlayer();
        User user = SpigotUtils.getMetaData( p, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            user = DonatorJoinPlus.i().getStorage().getUser( p.getUniqueId() );
        }

        final boolean toggled = user != null && user.isToggled();

        if ( SpigotUtils.isVanished( p ) || toggled )
        {
            return;
        }
        DonatorJoinPlus.i().debug( "Executing logout event for player " + p.getName() + "." );
        DonatorJoinEventHelper.executeEvent( user, false, null, p );
    }

    @EventHandler
    public void onWorldChange( PlayerChangedWorldEvent event )
    {
        final Player p = event.getPlayer();
        User user = SpigotUtils.getMetaData( p, SpigotUtils.USER_KEY, null );
        if ( user == null )
        {
            user = DonatorJoinPlus.i().getStorage().getUser( p.getUniqueId() );
            SpigotUtils.setMetaData( p, SpigotUtils.USER_KEY, user );
        }

        final boolean toggled = user != null && user.isToggled();

        if ( SpigotUtils.isVanished( p ) || toggled )
        {
            return;
        }
        DonatorJoinEventHelper.executeEvent( user, false, event.getFrom(), p );
        DonatorJoinEventHelper.executeEvent( user, true, event.getPlayer().getWorld(), p );
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
            log.error( "An error occured while loading a user with uuid " + uuid + ":", e );
            return DonatorJoinPlus.i().getStorage().getUser( uuid );
        }
    }
}