package com.dbsoftwares.djp.listeners;

import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.data.EventData;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

public class PlayerListener implements Listener {

    private final LoadingCache<UUID, CompletableFuture<Boolean>> loadingCache = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build(new CacheLoader<UUID, CompletableFuture<Boolean>>() {
                public CompletableFuture<Boolean> load(final UUID uuid) {
                    return CompletableFuture.supplyAsync(() -> DonatorJoinPlus.i().getStorage().isToggled(uuid));
                }
            });


    private DonatorJoinPlus plugin;

    public PlayerListener(DonatorJoinPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoad(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> DonatorJoinPlus.i().getStorage().isToggled(player.getUniqueId()));
        loadingCache.put(player.getUniqueId(), future);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        final boolean toggled = getToggledStatus(p.getUniqueId());
        Utils.setMetaData(p, Utils.TOGGLE_KEY, toggled);

        if (!plugin.isDisableJoinMessage()) {
            event.setJoinMessage(null);
        }

        if (Utils.isVanished(p) || toggled) {
            return;
        }

        executeEvent(true, p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player p = event.getPlayer();

        if (!plugin.isDisableJoinMessage()) {
            event.setQuitMessage(null);
        }
        if (Utils.isVanished(p) || (boolean) Utils.getMetaData(p, Utils.TOGGLE_KEY, false)) {
            return;
        }
        executeEvent(false, p);
    }

    private boolean getToggledStatus(final UUID uuid) {
        try {
            final CompletableFuture<Boolean> future = loadingCache.get(uuid);

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return DonatorJoinPlus.i().getStorage().isToggled(uuid);
        }
    }

    private void executeEvent(final boolean join, final Player p) {
        final String[] groups = plugin.getPermission().getPlayerGroups(p);
        for (RankData data : plugin.getRankData()) {
            final EventData eventData = join ? data.getJoin() : data.getQuit();

            if (plugin.isUsePermissions()) {
                if (plugin.getPermission().has(p, data.getPermission())) {
                    executeEventData(p, eventData);

                    if (plugin.getConfiguration().getBoolean("usepriorities")) {
                        break;
                    }
                }
            } else {
                if (Utils.contains(groups, data.getName())) {
                    executeEventData(p, eventData);

                    if (plugin.getConfiguration().getBoolean("usepriorities")) {
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
}