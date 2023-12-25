package dev.endoy.djp.spigot.integrations.vanish;

import dev.endoy.djp.spigot.integrations.Integration;
import org.bukkit.entity.Player;

public interface VanishIntegration extends Integration
{

    void register();

    boolean isVanished( Player player );

}
