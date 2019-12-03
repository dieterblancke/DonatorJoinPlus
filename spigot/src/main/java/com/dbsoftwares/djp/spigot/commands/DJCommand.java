package com.dbsoftwares.djp.spigot.commands;

import com.dbsoftwares.commandapi.command.MainSpigotCommand;
import com.dbsoftwares.commandapi.utils.MessageConfig;
import com.dbsoftwares.djp.spigot.commands.subcommands.ListSoundsSubCommand;
import com.dbsoftwares.djp.spigot.commands.subcommands.ReloadSubCommand;
import com.dbsoftwares.djp.spigot.commands.subcommands.SetSlotGroupSubCommand;
import com.dbsoftwares.djp.spigot.commands.subcommands.toggle.DisableSubCommand;
import com.dbsoftwares.djp.spigot.commands.subcommands.toggle.EnableSubCommand;
import com.dbsoftwares.djp.spigot.commands.subcommands.toggle.ToggleSubCommand;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Lists;

public class DJCommand extends MainSpigotCommand
{

    public DJCommand()
    {
        super( "donatorjoin", Lists.newArrayList( "dj", "djp", "donatorjoinplus" ) );

        subCommands.add( new ReloadSubCommand() );
        subCommands.add( new EnableSubCommand() );
        subCommands.add( new DisableSubCommand() );
        subCommands.add( new ToggleSubCommand() );
        subCommands.add( new SetSlotGroupSubCommand() );
        subCommands.add( new ListSoundsSubCommand() );

        loadMessageConfig();
    }

    private void loadMessageConfig()
    {
        final MessageConfig messageConfig = new MessageConfig();

        messageConfig.setNoPermissionMessage( Utils.getMessage( "no-perm" ) );
        messageConfig.setUsageMessage( Utils.getMessage( "usage" ) );

        setMessageConfig( messageConfig );
    }
}
