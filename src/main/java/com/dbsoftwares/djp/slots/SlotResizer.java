package com.dbsoftwares.djp.slots;

import com.dbsoftwares.djp.utils.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class SlotResizer {

    private int max;

    public SlotResizer() {
        this.max = Bukkit.getMaxPlayers();
    }

    private void resize() {
        final Class<?> craftServer = ReflectionUtils.getCraftBukkitClass("CraftServer");
        final Object playerList = ReflectionUtils.getHandle(craftServer, Bukkit.getServer());
        final Field maxPlayers = ReflectionUtils.getField(playerList.getClass().getSuperclass(), "maxPlayers");

        try {
            maxPlayers.set(playerList, max);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not update max player slots.", e);
        }
    }

    private int getOnlinePlayers() {
        // If there were to be a memory leak that keeps offline players loaded, it should get skipped here
        return (int) Bukkit.getOnlinePlayers().stream().filter(p -> p != null && p.isOnline()).count();
    }
}
