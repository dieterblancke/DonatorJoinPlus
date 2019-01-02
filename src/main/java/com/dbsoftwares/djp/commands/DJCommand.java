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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class DJCommand extends SpigotCommand {

    public DJCommand() {
        super("donatorjoin", Lists.newArrayList("dj", "djp", "donatorjoinplus"));
    }

    @Override
    public List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return null;
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(Utils.c("&eDonatorJoin&b+ &eHelp:"));
            player.sendMessage(Utils.c("&b- &e/djp reload"));
            player.sendMessage(Utils.c("&b- &e/djp toggle &7[player]"));
            player.sendMessage(Utils.c("&b- &e/djp enable &7[player]"));
            player.sendMessage(Utils.c("&b- &e/djp disable &7[player]"));
            return;
        }
        final String action = args[0];

        if (action.equalsIgnoreCase("reload")) {
            reload(player);
        } else if (action.equalsIgnoreCase("toggle")) {
            final boolean toggled = (boolean) Utils.getMetaData(player, Utils.TOGGLE_KEY, false);

            if (toggled) {
                disable(player);
            } else {
                enable(player);
            }
        } else if (action.equalsIgnoreCase("enable")) {
            enable(player);
        } else if (action.equalsIgnoreCase("disable")) {
            disable(player);
        }
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.c("&ePlease use &b/dj reload&e!"));
            return;
        }
        final String action = args[0];

        if (action.equalsIgnoreCase("reload")) {
            reload(sender);
        }
    }

    private void reload(final CommandSender sender) {
        if (sender.hasPermission("donatorjoin.reload")) {
            DonatorJoinPlus plugin = DonatorJoinPlus.getPlugin(DonatorJoinPlus.class);

            plugin.reloadConfig();
            plugin.loadConfig();

            sender.sendMessage(Utils.c("&eYou have reloaded the config!"));
        } else {
            sender.sendMessage(Utils.c("&eYou do not have the permission to do this!"));
        }
    }

    private void enable(final Player player) {
        // TODO
    }

    private void disable(final Player player) {
        // TODO
    }
}
