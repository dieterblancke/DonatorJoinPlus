package com.dbsoftwares.djp.commands;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DJCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("donatorjoin")) {
            if (args.length != 1) {
                sender.sendMessage(Utils.c("&ePlease use &b/dj reload&e!"));
                return false;
            }
            if (sender.hasPermission("donatorjoin.reload")) {
                DonatorJoinPlus.getPlugin(DonatorJoinPlus.class).reloadConfig();
                sender.sendMessage(Utils.c("&eYou have reloaded the config!"));
                return true;
            } else {
                sender.sendMessage(Utils.c("&eYou do not have the permission to do this!"));
            }
        }
        return false;
    }
}
