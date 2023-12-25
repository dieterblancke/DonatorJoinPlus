package dev.endoy.djp.spigot.listeners;

import be.dieterblancke.configuration.api.IConfiguration;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.slots.SlotLimit;
import dev.endoy.djp.spigot.slots.SlotResizer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SlotListener implements Listener
{

    @EventHandler
    public void onPlayerLogin( final PlayerLoginEvent event )
    {
        final IConfiguration config = DonatorJoinPlus.i().getConfiguration();
        final SlotResizer slotResizer = DonatorJoinPlus.i().getSlotResizer();

        if ( !config.getBoolean( "slotforcer.enabled" ) || !event.getResult().equals( PlayerLoginEvent.Result.KICK_FULL ) )
        {
            return;
        }
        final Player player = event.getPlayer();

        final CompletableFuture<String> future = CompletableFuture.supplyAsync( () -> DonatorJoinPlus.i().getStorage().getSlotGroup( player.getUniqueId() ) );
        try
        {
            final String slotGroup = future.get();

            if ( !slotGroup.equals( "none" ) )
            {
                final SlotLimit limit = DonatorJoinPlus.i().getSlotLimits().stream().filter( l -> l.getName().equalsIgnoreCase( slotGroup ) ).findFirst().orElse( null );

                if ( limit != null )
                {
                    // attempting slot grant
                    if ( slotResizer.grantSlot( player.getUniqueId(), limit ) )
                    {
                        event.allow();
                    }
                    return;
                }
            }
        }
        catch ( InterruptedException | ExecutionException e )
        {
            throw new RuntimeException( "Could not execute async group request ...", e );
        }

        // Permission checker, for if no group has been set.
        final Optional<SlotLimit> optionalLimit = DonatorJoinPlus.i().getSlotLimits().stream()
                .filter( l -> player.hasPermission( l.getPermission() ) )
                .max( Comparator.comparingInt( SlotLimit::getLimit ) );

        if ( optionalLimit.isPresent() )
        {
            final SlotLimit limit = optionalLimit.get();

            // attempting slot grant
            if ( slotResizer.grantSlot( player.getUniqueId(), limit ) )
            {
                event.allow();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit( final PlayerQuitEvent event )
    {
        handlePlayerLeave( event );
    }

    @EventHandler
    public void onPlayerKick( final PlayerKickEvent event )
    {
        handlePlayerLeave( event );
    }

    private void handlePlayerLeave( final PlayerEvent event )
    {
        final Player player = event.getPlayer();

        DonatorJoinPlus.i().getSlotResizer().removePlayer( player.getUniqueId() );
    }
}
