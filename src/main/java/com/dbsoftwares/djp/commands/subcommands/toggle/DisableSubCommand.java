package com.dbsoftwares.djp.commands.subcommands.toggle;

import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class DisableSubCommand extends ToggableSubCommand {

    public DisableSubCommand() {
        super("disable", 0, 1);
    }

    @Override
    public String getUsage() {
        return "/djp disable [player]";
    }

    @Override
    public void onExecute(final Player player, final String[] args) {
        if (args.length == 0) {
            final UUID uuid = player.getUniqueId();

            disable(uuid);
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
            disable(uuid);

            sender.sendMessage(Utils.getMessage("disabled-other").replace("{player}", args[0]));
        }
    }
}
