package com.dbsoftwares.djp.listeners;

import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.data.EventData;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

        if (!plugin.isDisableJoinMessage()) {
            event.setJoinMessage(null);
        }

        String[] groups = plugin.getPermission().getPlayerGroups(p);
        for (RankData data : plugin.getRankData()) {
            EventData eventData = data.getJoin();

            if (plugin.isUsePermissions()) {
                if (plugin.getPermission().has(p, data.getPermission())) {
                    executeEventData(p, eventData);

                    if (plugin.getConfig().getBoolean("usepriorities")) {
                        break;
                    }
                }
            } else {
                if (Utils.contains(groups, data.getName())) {
                    executeEventData(p, eventData);

                    if (plugin.getConfig().getBoolean("usepriorities")) {
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        if (!plugin.isDisableJoinMessage()) {
            event.setQuitMessage(null);
        }
        String[] groups = plugin.getPermission().getPlayerGroups(p);

        for (RankData data : plugin.getRankData()) {
            EventData eventData = data.getQuit();

            if (plugin.isUsePermissions()) {
                if (plugin.getPermission().has(p, data.getPermission())) {
                    executeEventData(p, eventData);

                    if (plugin.getConfig().getBoolean("usepriorities")) {
                        break;
                    }
                }
            } else {
                if (Utils.contains(groups, data.getName())) {
                    executeEventData(p, eventData);

                    if (plugin.getConfig().getBoolean("usepriorities")) {
                        break;
                    }
                }
            }
        }
    }

    private void executeEventData(Player p, EventData eventData) {
        if (eventData.isEnabled()) {
            String message = eventData.getMessage().replace("%player%", p.getName());
            message = Utils.c(message);

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((OfflinePlayer) p, message);
            }

            Bukkit.broadcastMessage(message);

            if (eventData.isFirework()) {
                Utils.spawnFirework(p.getLocation());
            }

            if (eventData.isSoundEnabled() && eventData.getSound() != null) {
                p.getWorld().playSound(p.getLocation(), eventData.getSound(), 20F, -20F);
            }
        }
    }
}