package com.dbsoftwares.djp.listeners;

import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.data.EventData;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

public class PlayerListener implements Listener {

    private final Map<String, CompletableFuture<Boolean>> futures = Maps.newHashMap();

    private DonatorJoinPlus plugin;

    public PlayerListener(DonatorJoinPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoad(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> DonatorJoinPlus.i().getStorage().isToggled(player.getName()));
        futures.put(player.getName(), future);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        final String name = p.getName();

        final boolean toggled = getToggledStatus(p.getName());
        Utils.setMetaData(p, Utils.TOGGLE_KEY, toggled);

        if (Utils.isVanished(p) || toggled) {
            return;
        }
        if (!plugin.isDisableJoinMessage()) {
            event.setJoinMessage(null);
        }

        final String[] groups = plugin.getPermission().getPlayerGroups(p);
        for (RankData data : plugin.getRankData()) {
            final EventData eventData = data.getJoin();

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
        final Player p = event.getPlayer();

        if (Utils.isVanished(p) || (boolean) Utils.getMetaData(p, Utils.TOGGLE_KEY)) {
            return;
        }

        if (!plugin.isDisableJoinMessage()) {
            event.setQuitMessage(null);
        }
        final String[] groups = plugin.getPermission().getPlayerGroups(p);

        for (RankData data : plugin.getRankData()) {
            final EventData eventData = data.getQuit();

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

    private void executeEventData(final Player p, final EventData eventData) {
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

    private boolean getToggledStatus(final String name) {
        if (!futures.containsKey(name) || futures.get(name).isCancelled()) {
            return DonatorJoinPlus.i().getStorage().isToggled(name);
        } else {
            final CompletableFuture<Boolean> future = futures.remove(name);

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return DonatorJoinPlus.i().getStorage().isToggled(name);
            }
        }
    }
}