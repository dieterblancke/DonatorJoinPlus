package com.dbsoftwares.djp.spigot.integrations.vanish;

import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import com.dbsoftwares.djp.spigot.utils.DonatorJoinEventHelper;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.user.User;
import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import de.myzelyam.api.vanish.VanishAPI;
import ir.syrent.velocityvanish.spigot.VelocityVanishSpigot;
import ir.syrent.velocityvanish.spigot.event.PostUnVanishEvent;
import ir.syrent.velocityvanish.spigot.event.PostVanishEvent;
import ir.syrent.velocityvanish.velocity.VelocityVanish;
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
