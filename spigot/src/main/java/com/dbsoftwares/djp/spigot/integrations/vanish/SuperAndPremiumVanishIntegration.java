package com.dbsoftwares.djp.spigot.integrations.vanish;

import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import com.dbsoftwares.djp.spigot.utils.DonatorJoinEventHelper;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.user.User;
import com.earth2me.essentials.Essentials;
import net.ess3.api.events.VanishStatusChangeEvent;
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
        Essentials essentials = Essentials.getPlugin( Essentials.class );

        return essentials.getUser( player ).isVanished() || essentials.getVanishedPlayersNew().contains( player.getName() );
    }

    @EventHandler
    public void onVanishStatusChange( VanishStatusChangeEvent event )
    {
        Player player = event.getAffected().getBase();
        User user = SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            return;
        }

        DonatorJoinEventHelper.executeEvent( user, !event.getValue(), null, player );
    }
}
