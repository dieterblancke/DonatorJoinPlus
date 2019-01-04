package com.dbsoftwares.djp.commands;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.commandapi.SpigotCommand;
import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DJCommand extends SpigotCommand {

    public DJCommand() {
        super("donatorjoin", Lists.newArrayList("dj", "djp", "donatorjoinplus"));
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (args.length == 1) {
            final String action = args[0];

            if (action.equalsIgnoreCase("reload")) {
                reload(player);
            } else {
                executeToggleFor(action, player, player, "donatorjoinplus.toggle");
            }
        } else if (args.length == 2) {
            final String action = args[0];
            final Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(getMessage("not-online"));
                return;
            }

            executeToggleFor(action, player, target, "donatorjoinplus.toggle.others");
        } else {
            sendHelp(player);
        }
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            final String action = args[0];

            if (action.equalsIgnoreCase("reload")) {
                reload(sender);
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 2) {
            final String action = args[0];
            final Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(getMessage("not-online"));
                return;
            }

            executeToggleFor(action, sender, target, "donatorjoinplus.toggle.others");
        } else {
            sendHelp(sender);
        }
    }

    private void reload(final CommandSender sender) {
        if (sender.hasPermission("donatorjoinplus.reload")) {
            DonatorJoinPlus plugin = DonatorJoinPlus.getPlugin(DonatorJoinPlus.class);

            plugin.reloadConfig();
            plugin.loadConfig();

            sender.sendMessage(getMessage("reloaded"));
        } else {
            sender.sendMessage(getMessage("no-perm"));
        }
    }

    private void enable(final Player player) {
        final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> DonatorJoinPlus.i().getStorage().toggle(player.getUniqueId(), false));
        future.thenRun(() -> {
            player.sendMessage(getMessage("enabled"));
            Utils.setMetaData(player, Utils.TOGGLE_KEY, false);
        });
    }

    private void disable(final Player player) {
        final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> DonatorJoinPlus.i().getStorage().toggle(player.getUniqueId(), true));
        future.thenRun(() -> {
            player.sendMessage(getMessage("disabled"));
            Utils.setMetaData(player, Utils.TOGGLE_KEY, true);
        });
    }

    private String getMessage(final String path) {
        final FileConfiguration config = DonatorJoinPlus.i().getConfig();

        return Utils.c(config.getString("messages.prefix") + config.getString("messages." + path));
    }

    private void executeToggleFor(final String action, final CommandSender sender, final Player target, final String permission) {
        if (action.equalsIgnoreCase("toggle")) {
            if (!sender.hasPermission(permission)) {
                sender.sendMessage(getMessage("no-perm"));
                return;
            }

            final boolean toggled = (boolean) Utils.getMetaData(target, Utils.TOGGLE_KEY, false);

            if (toggled) {
                enable(target);
            } else {
                disable(target);
            }
        } else if (action.equalsIgnoreCase("enable")) {
            if (!sender.hasPermission(permission)) {
                sender.sendMessage(getMessage("no-perm"));
                return;
            }

            enable(target);
        } else if (action.equalsIgnoreCase("disable")) {
            if (!sender.hasPermission(permission)) {
                sender.sendMessage(getMessage("no-perm"));
                return;
            }

            disable(target);
        } else {
            sendHelp(sender);
        }
    }

    private void sendHelp(final CommandSender sender) {
        sender.sendMessage(Utils.c("&eDonatorJoin&b+ &eHelp:"));
        sender.sendMessage(Utils.c("&b- &e/djp reload"));
        sender.sendMessage(Utils.c("&b- &e/djp toggle &7[player]"));
        sender.sendMessage(Utils.c("&b- &e/djp enable &7[player]"));
        sender.sendMessage(Utils.c("&b- &e/djp disable &7[player]"));
    }
}
