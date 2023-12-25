package dev.endoy.djp.spigot.commands.subcommands.toggle;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.commands.DJSubCommand;
import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.user.User;
import dev.endoy.djp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ToggleFireworkSubCommand extends DJSubCommand
{

    public ToggleFireworkSubCommand()
    {
        super( "togglefirework", 0, 1 );
    }

    @Override
    public String getUsage()
    {
        return "/djp togglefirework [player]";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.togglefirework";
    }

    @Override
    public void onExecute( Player player, String[] args )
    {
        if ( args.length > 1 )
        {
            player.sendMessage( Utils.getMessage( "firework.toggle.usage" ) );
            return;
        }
        if ( args.length == 1 )
        {
            onExecute( (CommandSender) player, args );
            return;
        }
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );
        boolean toggled = !user.isFireworkToggled();

        user.setFireworkToggled( toggled );
        DonatorJoinPlus.i().getStorage().toggleFirework( player.getUniqueId(), toggled );
        player.sendMessage( Utils.getMessage( "firework.toggle." + ( toggled ? "disabled" : "enabled" ) ) );
    }

    @Override
    public void onExecute( CommandSender sender, String[] args )
    {
        if ( args.length != 1 )
        {
            sender.sendMessage( Utils.getMessage( "firework.toggle.console-usage" ) );
            return;
        }
        if ( !sender.hasPermission( "donatorjoinplus.togglefirework.other" ) )
        {
            sender.sendMessage( Utils.getMessage( "no-perm" ) );
            return;
        }
        String playerName = args[0];
        UUID uuid = SpigotUtils.getUuid( args[0] );
        if ( uuid == null )
        {
            sender.sendMessage( Utils.getMessage( "never-joined" ) );
            return;
        }
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( uuid );
        boolean toggled = !user.isFireworkToggled();

        user.setFireworkToggled( toggled );
        DonatorJoinPlus.i().getStorage().toggleFirework( uuid, toggled );

        Player target = Bukkit.getPlayer( uuid );
        if ( target != null )
        {
            target.sendMessage( Utils.getMessage( "firework.toggle." + ( toggled ? "disabled" : "enabled" ) ) );
        }

        sender.sendMessage(
                Utils.getMessage( "firework.toggle." + ( toggled ? "disabled" : "enabled" ) + "-other" )
                        .replace( "{player}", args[0] )
        );
    }

    @Override
    public List<String> getCompletions( CommandSender sender, String[] args )
    {
        return getPlayerCompletions( args );
    }

    @Override
    public List<String> getCompletions( Player player, String[] args )
    {
        return getCompletions( (CommandSender) player, args );
    }
}
