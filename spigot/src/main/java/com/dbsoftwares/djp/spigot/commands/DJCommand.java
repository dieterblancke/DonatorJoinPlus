package com.dbsoftwares.djp.spigot.commands;

import com.dbsoftwares.commandapi.command.MainSpigotCommand;
import com.dbsoftwares.commandapi.command.SubCommand;
import com.dbsoftwares.commandapi.utils.MessageConfig;
import com.dbsoftwares.djp.spigot.commands.subcommands.*;
import com.dbsoftwares.djp.spigot.commands.subcommands.toggle.*;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;

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
        subCommands.add( new JoinSoundSubCommand() );
        subCommands.add( new LeaveSoundSubCommand() );
        subCommands.add( new ToggleSoundSubCommand() );
        subCommands.add( new ToggleFireworkSubCommand() );
        subCommands.add( new ToggleMessageSubCommand() );

        loadMessageConfig();
    }

    private void loadMessageConfig()
    {
        final MessageConfig messageConfig = new MessageConfig();

        messageConfig.setNoPermissionMessage( Utils.getMessage( "no-perm" ) );
        messageConfig.setUsageMessage( Utils.getMessage( "usage" ) );

        setMessageConfig( messageConfig );
    }

    @Override
    public void onExecute( CommandSender sender, String[] args )
    {
        if ( args.length != 0 )
        {
            for ( SubCommand subCommand : subCommands )
            {
                if ( subCommand.execute( sender, args, getMessageConfig() ) )
                {
                    return;
                }
            }
        }

        sender.sendMessage( Utils.getMessage( "help" ) );
    }
}
