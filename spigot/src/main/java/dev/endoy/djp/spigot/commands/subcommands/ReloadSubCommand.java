package dev.endoy.djp.spigot.commands.subcommands;

import com.dbsoftwares.commandapi.command.SubCommand;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.utils.Utils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadSubCommand extends SubCommand
{

    public ReloadSubCommand()
    {
        super( "reload" );
    }

    @Override
    public String getUsage()
    {
        return "/djp reload";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.reload";
    }

    @Override
    public void onExecute( final Player player, final String[] args )
    {
        this.onExecute( (CommandSender) player, args );
    }

    @Override
    public void onExecute( final CommandSender sender, final String[] args )
    {
        final DonatorJoinPlus plugin = DonatorJoinPlus.i();

        plugin.loadConfig();
        plugin.reloadConfig();

        sender.sendMessage( Utils.getMessage( "reloaded" ) );
    }

    @Override
    public List<String> getCompletions( final CommandSender sender, final String[] args )
    {
        return ImmutableList.of();
    }

    @Override
    public List<String> getCompletions( final Player player, final String[] args )
    {
        return ImmutableList.of();
    }
}
