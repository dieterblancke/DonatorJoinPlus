package dev.endoy.djp.spigot.integrations.vanish;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.DonatorJoinEventHelper;
import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.user.User;
import ir.syrent.velocityvanish.spigot.VelocityVanishSpigot;
import ir.syrent.velocityvanish.spigot.event.PostUnVanishEvent;
import ir.syrent.velocityvanish.spigot.event.PostVanishEvent;
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
        return VelocityVanishSpigot.instance.getVanishedNames().contains( player.getName() );
    }

    @EventHandler
    public void afterVanishedEvent( PostVanishEvent event )
    {
        Player player = event.getPlayer();
        User user = SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            return;
        }

        DonatorJoinEventHelper.executeEvent( user, false, null, player );
    }

    @EventHandler
    public void afterUnvanishedEvent( PostUnVanishEvent event )
    {
        Player player = event.getPlayer();
        User user = SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            return;
        }

        DonatorJoinEventHelper.executeEvent( user, true, null, player );
    }
}
