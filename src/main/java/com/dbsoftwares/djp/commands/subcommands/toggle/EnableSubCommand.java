package com.dbsoftwares.djp.commands.subcommands.toggle;

import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class EnableSubCommand extends ToggableSubCommand {

    public EnableSubCommand() {
        super("enable", 0, 1);
    }

    @Override
    public String getUsage() {
        return "/djp enable [player]";
    }

    @Override
    public void onExecute(final Player player, final String[] args) {
        if (args.length == 0) {
            final UUID uuid = player.getUniqueId();

            enable(uuid);
        } else {
            onExecute((CommandSender) player, args);
        }
    }

    @Override
    public void onExecute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("not-for-console"));
        } else {
            if (!sender.hasPermission(getPermission() + ".others")) {
                sender.sendMessage(Utils.getMessage("no-perm"));
                return;
            }

            final UUID uuid = Utils.getUuid(args[0]);
            if (uuid == null) {
                sender.sendMessage(Utils.getMessage("never-joined"));
                return;
            }
            enable(uuid);

            sender.sendMessage(Utils.getMessage("enabled-other").replace("{player}", args[0]));
        }
    }
}
