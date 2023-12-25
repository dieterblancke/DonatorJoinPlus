package dev.endoy.djp.spigot.commands.subcommands.toggle;

import com.dbsoftwares.commandapi.command.SubCommand;
import com.google.common.collect.ImmutableList;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.user.User;
import dev.endoy.djp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ToggableSubCommand extends SubCommand
{

    public ToggableSubCommand( final String name )
    {
        this( name, 0, 0 );
    }

    public ToggableSubCommand( final String name, final int minimumArgs )
    {
        this( name, minimumArgs, minimumArgs );
    }

    public ToggableSubCommand( final String name, final int minimumArgs, final int maximumArgs )
    {
        super( name, minimumArgs, maximumArgs );
    }

    @Override
    public String getUsage()
    {
        return "/djp toggle [player]";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.toggle";
    }

    @Override
    public void onExecute( final Player player, final String[] args )
    {
        if ( args.length == 0 )
        {
            User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

            toggle( player.getUniqueId(), user.isToggled() );
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

            final UUID uuid = SpigotUtils.getUuid( args[0] );
            if ( uuid == null )
            {
                sender.sendMessage( Utils.getMessage( "never-joined" ) );
                return;
            }
            Player target = Bukkit.getPlayer( uuid );
            User targetUser = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( target.getUniqueId() );

            toggle( uuid, targetUser.isToggled() );
        }
    }

    protected void toggle( UUID uuid, boolean toggled )
    {
        CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggle( uuid, toggled ) )
                .thenRun( () -> DonatorJoinPlus.i().getUserManager().getUser( uuid ).ifPresent( user ->
                {
                    user.setToggled( false );

                    Player player = Bukkit.getPlayer( uuid );
                    if ( player != null )
                    {
                        player.sendMessage( Utils.getMessage( toggled ? "enabled" : "disabled" ) );
                    }
                } ) );
    }

    @Override
    public List<String> getCompletions( final Player player, final String[] args )
    {
        return getCompletions( (CommandSender) player, args );
    }

    @Override
    public List<String> getCompletions( final CommandSender sender, final String[] args )
    {
        if ( sender.hasPermission( getPermission() + ".others" ) )
        {
            return null;
        }
        return ImmutableList.of();
    }
}
