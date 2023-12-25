package dev.endoy.djp.spigot.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.user.User;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserManager
{

    private final Cache<UUID, CompletableFuture<User>> loadingCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 15, TimeUnit.SECONDS )
            .build();
    private final Cache<UUID, User> userCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 1, TimeUnit.HOURS )
            .build();

    public UserManager()
    {
        // Cleanup task, runs every 3 minutes to ensure the PlayerListener loadingCache is cleaned up.
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                DonatorJoinPlus.i().debug( String.format( "Cleaning up loading cache ... [initialSize = %s]", loadingCache.size() ) );
                loadingCache.cleanUp();
                DonatorJoinPlus.i().debug( String.format( "Successfully cleaned up loading cache ... [currentSize = %s]", loadingCache.size() ) );
            }
        }.runTaskTimerAsynchronously( DonatorJoinPlus.i(), 3600, 3600 );
    }

    public CompletableFuture<User> loadUser( UUID uuid )
    {
        CompletableFuture<User> completableFuture = CompletableFuture.supplyAsync( () -> DonatorJoinPlus.i().getStorage().getUser( uuid ) );
        completableFuture.thenAccept( user -> this.userCache.put( uuid, user ) );
        loadingCache.put( uuid, completableFuture );
        return completableFuture;
    }

    public Optional<User> getUser( UUID uuid )
    {
        return Optional.ofNullable( userCache.getIfPresent( uuid ) );
    }

    public User getOrLoadUserSync( UUID uuid )
    {
        return getOrLoadUser( uuid ).join();
    }

    public CompletableFuture<User> getOrLoadUser( UUID uuid )
    {
        if ( userCache.asMap().containsKey( uuid ) )
        {
            return CompletableFuture.completedFuture( userCache.getIfPresent( uuid ) );
        }
        else
        {
            if ( loadingCache.asMap().containsKey( uuid ) )
            {
                return loadingCache.getIfPresent( uuid );
            }
            else
            {
                return loadUser( uuid );
            }
        }
    }
}
