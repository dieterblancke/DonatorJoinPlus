package dev.endoy.djp.spigot.integrations.vanish;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.DonatorJoinEventHelper;
import dev.endoy.djp.user.User;
import com.earth2me.essentials.Essentials;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsVanishIntegration implements VanishIntegration, Listener
{

    @Override
    public void register()
    {
        Bukkit.getPluginManager().registerEvents( this, DonatorJoinPlus.i() );
    }

    @Override
    public boolean isVanished( Player player )
    {
        DonatorJoinPlus.i().debug( "Checking if user is vanished by Essentials." );
        Essentials essentials = Essentials.getPlugin( Essentials.class );

        return essentials.getUser( player ).isVanished() || essentials.getVanishedPlayersNew().contains( player.getName() );
    }

    @EventHandler
    public void onVanishStatusChange( VanishStatusChangeEvent event )
    {
        Player player = event.getAffected().getBase();
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        DonatorJoinPlus.i().debug( "Essentials vanish status changed to " + event.getValue() + "." );

        DonatorJoinEventHelper.executeEvent( user, !event.getValue(), null, player );
    }
}
