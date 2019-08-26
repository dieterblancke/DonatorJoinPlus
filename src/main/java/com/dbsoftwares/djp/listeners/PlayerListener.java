package com.dbsoftwares.djp.listeners;

import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.data.EventData;
import com.dbsoftwares.djp.data.EventData.EventType;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
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
                @ParametersAreNonnullByDefault
                public CompletableFuture<Boolean> load(final UUID uuid) {
                    return CompletableFuture.supplyAsync(() -> DonatorJoinPlus.i().getStorage().isToggled(uuid));
                }
            });

    public PlayerListener() {
        // Cleanup task, runs every 3 minutes to ensure the PlayerListener loadingCache is cleaned up.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (DonatorJoinPlus.i().isDebugMode()) {
                    DonatorJoinPlus.getLog().debug("Cleaning up loading cache ... [initialSize={}]", loadingCache.size());
                }
                loadingCache.cleanUp();

                if (DonatorJoinPlus.i().isDebugMode()) {
                    DonatorJoinPlus.getLog().debug("Successfully up loading cache ... [currentSize={}]", loadingCache.size());
                }
            }
        }.runTaskTimerAsynchronously(DonatorJoinPlus.i(), 3600, 3600);
    }

    @EventHandler
    public void onLoad(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        DonatorJoinPlus.i().debug("Initializing loading of storage for player " + player.getName() + ".");

        final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> DonatorJoinPlus.i().getStorage().isToggled(player.getUniqueId()));
        loadingCache.put(player.getUniqueId(), future);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        final boolean toggled = getToggledStatus(p.getUniqueId());
        Utils.setMetaData(p, Utils.TOGGLE_KEY, toggled);

        if (!DonatorJoinPlus.i().isDisableJoinMessage()) {
            event.setJoinMessage(null);
        }

        if (Utils.isVanished(p) || toggled) {
            return;
        }

        DonatorJoinPlus.i().debug("Executing login event for player " + p.getName() + ".");

        executeEvent(true, null, p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!DonatorJoinPlus.i().isDisableQuitMessage()) {
            event.setQuitMessage(null);
        }

        final Player p = event.getPlayer();

        if (Utils.isVanished(p) || (boolean) Utils.getMetaData(p, Utils.TOGGLE_KEY, false)) {
            return;
        }
        executeEvent(false, null, p);
        DonatorJoinPlus.i().debug("Executing logout event for player " + p.getName() + ".");
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        final Player p = event.getPlayer();

        if (Utils.isVanished(p) || (boolean) Utils.getMetaData(p, Utils.TOGGLE_KEY, false)) {
            return;
        }
        executeEvent(false, event.getFrom(), p);
        executeEvent(true, event.getPlayer().getWorld(), p);
    }

    private boolean getToggledStatus(final UUID uuid) {
        try {
            final CompletableFuture<Boolean> future = loadingCache.get(uuid);

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            return DonatorJoinPlus.i().getStorage().isToggled(uuid);
        }
    }

    private void executeEvent(final boolean join, final World world, final Player p) {
        final String[] groups = DonatorJoinPlus.i().getPermission().getPlayerGroups(p);

        DonatorJoinPlus.i().debug("List of groups for player " + p.getName() + ": " + Arrays.toString(groups));

        for (RankData data : DonatorJoinPlus.i().getRankData()) {
            final EventType type = join ? EventType.JOIN : EventType.QUIT;
            final EventData eventData = (world != null ? data.getWorldEvents() : data.getEvents()).getOrDefault(type, null);

            if (eventData == null) {
                continue;
            }

            if (DonatorJoinPlus.i().isUsePermissions()) {
                if (DonatorJoinPlus.i().getPermission().has(p, data.getPermission())) {
                    DonatorJoinPlus.i().debug("Player " + p.getName() + " has the permission " + data.getPermission() + ", executing event ...");

                    executeEventData(p, eventData, world);

                    if (DonatorJoinPlus.i().getConfiguration().getBoolean("usepriorities")) {
                        break;
                    }
                } else {
                    DonatorJoinPlus.i().debug("Player " + p.getName() + " does not have the permission " + data.getPermission() + ".");
                }
            } else {
                if (Utils.contains(groups, data.getName())) {
                    DonatorJoinPlus.i().debug("Player " + p.getName() + " is in the group " + data.getName() + ", executing event ...");
                    executeEventData(p, eventData, world);

                    if (DonatorJoinPlus.i().getConfiguration().getBoolean("usepriorities")) {
                        break;
                    }
                }
            }
        }
    }

    private void executeEventData(final Player p, final EventData eventData, final World world) {
        if (eventData.isEnabled()) {
            final String message = formatString(p, eventData.getMessage());

            for (String msg : message.split("<nl>")) {
                if (world != null) {
                    for (Player player : world.getPlayers()) {
                        player.sendMessage(msg);
                    }
                    Bukkit.getConsoleSender().sendMessage(msg);
                } else {
                    Bukkit.broadcastMessage(msg);
                }
            }

            if (eventData.isFirework()) {
                Utils.spawnFirework(p.getLocation());
            }

            if (eventData.isSoundEnabled() && eventData.getSound() != null) {
                p.getWorld().playSound(p.getLocation(), eventData.getSound(), 20F, -20F);
            }

            if (eventData.isCommandsEnabled() && eventData.getCommands() != null && !eventData.getCommands().isEmpty()) {
                for (String command : eventData.getCommands()) {
                    command = formatString(p, command);

                    DonatorJoinPlus.i().debug("Executing command " + command + " for player " + p.getName() + ".");

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }
    }

    private String formatString(final Player p, String str) {
        str = str.replace("%player%", p.getName());
        str = Utils.c(str);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            str = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((OfflinePlayer) p, str);
        }
        return str;
    }
}