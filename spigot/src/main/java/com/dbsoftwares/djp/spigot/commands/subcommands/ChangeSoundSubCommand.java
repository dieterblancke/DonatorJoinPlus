package com.dbsoftwares.djp.spigot.commands.subcommands;

import com.dbsoftwares.commandapi.command.SubCommand;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChangeSoundSubCommand extends SubCommand
{

    public ChangeSoundSubCommand()
    {
        super( "changesound", 1, 2 );
    }

    @Override
    public String getUsage()
    {
        return "/djp toggle [player]";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.changesound";
    }

    @Override
    public void onExecute( Player player, String[] args )
    {
        if (args.length > 2) {
            player.sendMessage( Utils.getMessage( "changesound.usage" ) );
            return;
        }
        if (args.length == 2) {
            onExecute( (CommandSender) player, args );
            return;
        }

    }

    @Override
    public void onExecute( CommandSender sender, String[] args )
    {
        if (args.length != 2) {
            sender.sendMessage( Utils.getMessage( "changesound.console-usage" ) );
            return;
        }
    }

    @Override
    public List<String> getCompletions( CommandSender sender, String[] args )
    {
        return null;
    }

    @Override
    public List<String> getCompletions( Player player, String[] args )
    {
        return null;
    }
}
