package dev.endoy.djp.spigot.commands.subcommands.toggle;

import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DisableSubCommand extends ToggableSubCommand
{

    public DisableSubCommand()
    {
        super( "disable", 0, 1 );
    }

    @Override
    public String getUsage()
    {
        return "/djp disable [player]";
    }

    @Override
    public void onExecute( final Player player, final String[] args )
    {
        if ( args.length == 0 )
        {
            toggle( player.getUniqueId(), false );
        }
        else
        {
            onExecute( (CommandSender) player, args );
        }
    }

    @Override
    public void onExecute( final CommandSender sender, final String[] args )
    {
        if ( args.length == 0 )
        {
            sender.sendMessage( Utils.getMessage( "not-for-console" ) );
        }
        else
        {
            if ( !sender.hasPermission( getPermission() + ".others" ) )
            {
                sender.sendMessage( Utils.getMessage( "no-perm" ) );
                return;
            }

            UUID uuid = SpigotUtils.getUuid( args[0] );
            if ( uuid == null )
            {
                sender.sendMessage( Utils.getMessage( "never-joined" ) );
                return;
            }
            toggle( uuid, true );

            sender.sendMessage( Utils.getMessage( "disabled-other" ).replace( "{player}", args[0] ) );
        }
    }
}
