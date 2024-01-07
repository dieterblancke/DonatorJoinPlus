package dev.endoy.djp.spigot.integrations.vanish;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.Constants;
import dev.endoy.djp.spigot.utils.DonatorJoinEventHelper;
import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.user.User;
import ir.syrent.velocityvanish.spigot.VelocityVanishSpigot;
import ir.syrent.velocityvanish.spigot.event.PreUnVanishEvent;
import ir.syrent.velocityvanish.spigot.event.PreVanishEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VelocityVanishIntegration implements VanishIntegration, Listener
{

    @Override
    public void register()
    {
        Bukkit.getPluginManager().registerEvents( this, DonatorJoinPlus.i() );
    }

    @Override
    public boolean isVanished( Player player )
    {
        DonatorJoinPlus.i().debug( "Checking if player is vanished by VelocityVanish." );

        Long joinTime = SpigotUtils.getMetaData( player, Constants.DJP_JOIN_TIME_KEY );

        if ( joinTime == null || joinTime + 3000 < System.currentTimeMillis() )
        {
            return VelocityVanishSpigot.instance.getVanishedNames().contains( player.getName() );
        }
        else
        {
            return true;
        }
    }

    @EventHandler
    public void afterVanishedEvent( PreVanishEvent event )
    {
        Player player = event.getPlayer();
        Long joinTime = SpigotUtils.getMetaData( player, Constants.DJP_JOIN_TIME_KEY );
        if ( joinTime != null && joinTime + 3000 > System.currentTimeMillis() )
        {
            return;
        }

        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        DonatorJoinPlus.i().debug( "Player got vanished by VelocityVanish, sending quit message." );

        event.setSendQuitMessage( false );
        DonatorJoinEventHelper.executeEvent( user, false, null, player );
    }

    @EventHandler
    public void afterUnvanishedEvent( PreUnVanishEvent event )
    {
        Player player = event.getPlayer();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        DonatorJoinPlus.i().debug( "Player got unvanished by VelocityVanish, sending join message." );

        event.setSendJoinMessage( false );
        DonatorJoinEventHelper.executeEvent( user, true, null, player );
    }
}
