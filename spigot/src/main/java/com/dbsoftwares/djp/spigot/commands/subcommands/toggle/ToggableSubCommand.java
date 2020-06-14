package com.dbsoftwares.djp.spigot.commands.subcommands.toggle;

import com.dbsoftwares.commandapi.command.SubCommand;
import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.user.User;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.ImmutableList;
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
            final UUID uuid = player.getUniqueId();
            final User user = (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

            if ( user == null )
            {
                return;
            }
            final boolean toggled = user.isToggled();

            if ( toggled )
            {
                enable( uuid );
            }
            else
            {
                disable( uuid );
            }
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
            final Player target = Bukkit.getPlayer( uuid );
            final User user = (User) SpigotUtils.getMetaData( target, SpigotUtils.USER_KEY, null );

            final boolean toggled;
            if ( target == null || user == null )
            {
                toggled = DonatorJoinPlus.i().getStorage().isToggled( uuid );
            }
            else
            {
                toggled = user.isToggled();
            }

            if ( toggled )
            {
                enable( uuid );
            }
            else
            {
                disable( uuid );
            }
        }
    }

    protected void enable( final UUID uuid )
    {
        final CompletableFuture<Void> future = CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggle( uuid, false ) );
        future.thenRun( () ->
        {
            final Player player = Bukkit.getPlayer( uuid );

            if ( player != null && player.isOnline() )
            {
                final User user = (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

                if ( user != null )
                {
                    user.setToggled( false );

                    player.sendMessage( Utils.getMessage( "enabled" ) );
                }
            }
        } );
    }

    protected void disable( final UUID uuid )
    {
        final CompletableFuture<Void> future = CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggle( uuid, true ) );
        future.thenRun( () ->
        {
            final Player player = Bukkit.getPlayer( uuid );

            if ( player != null && player.isOnline() )
            {
                final User user = (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

                if ( user != null )
                {
                    user.setToggled( true );

                    player.sendMessage( Utils.getMessage( "disabled" ) );
                }
            }
        } );
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
