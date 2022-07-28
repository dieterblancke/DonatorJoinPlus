package com.dbsoftwares.djp.spigot.listeners;

import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.user.User;
import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VanishListener extends DJPListener implements Listener
{

    @EventHandler
    public void onPlayerShow( PlayerShowEvent event )
    {
        Player player = event.getPlayer();
        User user = SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            return;
        }

        executeEvent( user, true, null, player );
    }

    @EventHandler
    public void onPlayerHide( PlayerHideEvent event )
    {
        Player player = event.getPlayer();
        User user = SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            return;
        }

        executeEvent( user, false, null, player );
    }
}
