package com.dbsoftwares.djp.spigot.integrations.vanish;

import org.bukkit.entity.Player;

public class NoOpVanishIntegration implements VanishIntegration
{

    @Override
    public void register()
    {

    }

    @Override
    public boolean isVanished( Player player )
    {
        return false;
    }
}
