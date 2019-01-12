package com.dbsoftwares.djp.commands.subcommands;

import com.dbsoftwares.commandapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSlotGroupSubCommand extends SubCommand {

    public SetSlotGroupSubCommand() {
        super("setslotgroup", 2);
    }

    @Override
    public String getUsage() {
        return "/djp setslotgroup (player) (groupname / none)";
    }

    @Override
    public String getPermission() {
        return "donatorjoinplus.setslotgroup";
    }

    @Override
    public void onExecute(final Player player, final String[] args) {
        onExecute((CommandSender) player, args);
    }

    @Override
    public void onExecute(final CommandSender sender, final String[] args) {
        // TODO
    }

    @Override
    public List<String> getCompletions(final CommandSender sender, final String[] args) {
        return null;
    }

    @Override
    public List<String> getCompletions(final Player player, final String[] args) {
        return null;
    }
}
