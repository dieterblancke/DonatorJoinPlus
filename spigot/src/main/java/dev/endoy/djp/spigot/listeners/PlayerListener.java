package dev.endoy.djp.spigot.listeners;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.DonatorJoinEventHelper;
import dev.endoy.djp.spigot.utils.MessageBuilder;
import dev.endoy.djp.user.User;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Slf4j
public class PlayerListener implements Listener
{

    @EventHandler
    public void onFirstJoin( final PlayerJoinEvent event )
    {
        if ( !DonatorJoinPlus.i().getConfiguration().getBoolean( "firstjoin.enabled", false ) )
        {
            return;
        }

        Player player = event.getPlayer();
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
        DonatorJoinPlus.i().getUserManager().loadUser( player.getUniqueId() );
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onJoin( PlayerJoinEvent event )
    {
        Player p = event.getPlayer();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( p.getUniqueId() );

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

    @EventHandler( priority = EventPriority.MONITOR )
    public void onQuit( PlayerQuitEvent event )
    {
        if ( !DonatorJoinPlus.i().isDisableQuitMessage() )
        {
            event.setQuitMessage( null );
        }

        Player p = event.getPlayer();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( p.getUniqueId() );

        if ( DonatorJoinPlus.i().getVanishIntegration().isVanished( p ) || user.isToggled() )
        {
            return;
        }

        DonatorJoinPlus.i().debug( "Executing logout event for player " + p.getName() + "." );
        DonatorJoinEventHelper.executeEvent( user, false, null, p );
    }

    @EventHandler
    public void onWorldChange( PlayerChangedWorldEvent event )
    {
        Player p = event.getPlayer();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( p.getUniqueId() );

        if ( DonatorJoinPlus.i().getVanishIntegration().isVanished( p ) || user.isToggled() )
        {
            return;
        }

        DonatorJoinEventHelper.executeEvent( user, false, event.getFrom(), p );
        DonatorJoinEventHelper.executeEvent( user, true, event.getPlayer().getWorld(), p );
    }
}