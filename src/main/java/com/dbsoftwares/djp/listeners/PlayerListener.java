package com.dbsoftwares.djp.listeners;

import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.data.EventData;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

public class PlayerListener implements Listener {

    private DonatorJoinPlus plugin;

    public PlayerListener(DonatorJoinPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        for (RankData data : plugin.getRankData()) {
            String[] groups = plugin.getPermission().getPlayerGroups(p);

            if (Utils.contains(groups, data.getName())) {
                EventData eventData = data.getJoin();

                if (eventData.isEnabled()) {
                    if (!plugin.isDisableJoinMessage()) {
                        event.setJoinMessage(null);
                    }

                    Bukkit.broadcastMessage(Utils.c(eventData.getMessage().replace("%player%", p.getName())));

                    if (eventData.isFirework()) {
                        Utils.spawnFirework(p.getLocation());
                    }

                    if (eventData.isSoundEnabled()) {
                        p.getWorld().playSound(p.getLocation(), eventData.getSound(), 20F, -20F);
                    }
                }

                if (plugin.getConfig().getBoolean("usepriorities")) {
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        for (RankData data : plugin.getRankData()) {
            String[] groups = plugin.getPermission().getPlayerGroups(p);

            if (Utils.contains(groups, data.getName())) {
                EventData eventData = data.getQuit();

                if (eventData.isEnabled()) {
                    if (!plugin.isDisableJoinMessage()) {
                        event.setQuitMessage(null);
                    }

                    Bukkit.broadcastMessage(Utils.c(eventData.getMessage().replace("%player%", p.getName())));

                    if (eventData.isFirework()) {
                        Utils.spawnFirework(p.getLocation());
                    }

                    if (eventData.isSoundEnabled()) {
                        p.getWorld().playSound(p.getLocation(), eventData.getSound(), 20F, -20F);
                    }
                }

                if (plugin.getConfig().getBoolean("usepriorities")) {
                    break;
                }
            }
        }
    }
}