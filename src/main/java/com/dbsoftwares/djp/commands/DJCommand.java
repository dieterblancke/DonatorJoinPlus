package com.dbsoftwares.djp.commands;

import com.dbsoftwares.commandapi.command.MainSpigotCommand;
import com.dbsoftwares.commandapi.utils.MessageConfig;
import com.dbsoftwares.djp.commands.subcommands.toggle.DisableSubCommand;
import com.dbsoftwares.djp.commands.subcommands.toggle.EnableSubCommand;
import com.dbsoftwares.djp.commands.subcommands.ReloadSubCommand;
import com.dbsoftwares.djp.commands.subcommands.SetSlotGroupSubCommand;
import com.dbsoftwares.djp.commands.subcommands.toggle.ToggleSubCommand;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Lists;

public class DJCommand extends MainSpigotCommand {

    public DJCommand() {
        super("donatorjoin", Lists.newArrayList("dj", "djp", "donatorjoinplus"));

        subCommands.add(new ReloadSubCommand());
        subCommands.add(new EnableSubCommand());
        subCommands.add(new DisableSubCommand());
        subCommands.add(new ToggleSubCommand());
        subCommands.add(new SetSlotGroupSubCommand());

        loadMessageConfig();
    }

    private void loadMessageConfig() {
        final MessageConfig messageConfig = new MessageConfig();

        messageConfig.setNoPermissionMessage(Utils.getMessage("no-perm"));
        messageConfig.setUsageMessage(Utils.getMessage("usage"));

        setMessageConfig(messageConfig);
    }
}
