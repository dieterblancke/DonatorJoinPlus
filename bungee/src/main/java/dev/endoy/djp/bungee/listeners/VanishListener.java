package dev.endoy.djp.bungee.listeners;

import dev.endoy.djp.bungee.data.EventData;
import dev.endoy.djp.bungee.utils.BungeeUtils;
import dev.endoy.djp.bungee.utils.PlayerStorageData;
import de.myzelyam.api.vanish.BungeePlayerHideEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class VanishListener extends DJPListener implements Listener
{

    @EventHandler
    public void onPlayerShow( BungeePlayerHideEvent event )
    {
        ProxiedPlayer player = event.getPlayer();
        PlayerStorageData storageData = BungeeUtils.get( player );

        execute( player, EventData.EventType.JOIN );
    }

    @EventHandler
    public void onPlayerHide( BungeePlayerHideEvent event )
    {
        ProxiedPlayer player = event.getPlayer();
        PlayerStorageData storageData = BungeeUtils.get( player );

        execute( player, EventData.EventType.QUIT );
    }
}
