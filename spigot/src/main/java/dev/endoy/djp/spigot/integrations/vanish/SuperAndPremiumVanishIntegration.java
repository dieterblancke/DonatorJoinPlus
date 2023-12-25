package dev.endoy.djp.spigot.integrations.vanish;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.DonatorJoinEventHelper;
import dev.endoy.djp.user.User;
import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SuperAndPremiumVanishIntegration implements VanishIntegration, Listener
{

    @Override
    public void register()
    {
        Bukkit.getPluginManager().registerEvents( this, DonatorJoinPlus.i() );
    }

    @Override
    public boolean isVanished( Player player )
    {
        return VanishAPI.isInvisible( player );
    }

    @EventHandler
    public void onPlayerShow( PlayerShowEvent event )
    {
        Player player = event.getPlayer();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        DonatorJoinEventHelper.executeEvent( user, true, null, player );
    }

    @EventHandler
    public void onPlayerHide( PlayerHideEvent event )
    {
        Player player = event.getPlayer();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        DonatorJoinEventHelper.executeEvent( user, false, null, player );
    }
}
