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

public class ToggleMessageSubCommand extends SubCommand
{

    public ToggleMessageSubCommand()
    {
        super( "togglemessages", 0, 0 );
    }

    @Override
    public String getUsage()
    {
        return "/djp togglemessages";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.togglemessages";
    }

    @Override
    public void onExecute( final Player player, final String[] args )
    {
        final UUID uuid = player.getUniqueId();
        final User user = (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

        if ( user == null )
        {
            return;
        }
        final boolean muted = user.isMessagesMuted();

        if ( muted )
        {
            disable( uuid );
        }
        else
        {
            enable( uuid );
        }
    }

    @Override
    public void onExecute( final CommandSender sender, final String[] args )
    {
        sender.sendMessage( Utils.getMessage( "not-for-console" ) );
    }

    protected void enable( final UUID uuid )
    {
        final CompletableFuture<Void> future = CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggleMessagesMuted( uuid, true ) );
        future.thenRun( () ->
        {
            final Player player = Bukkit.getPlayer( uuid );

            if ( player != null && player.isOnline() )
            {
                final User user = (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

                if ( user != null )
                {
                    user.setMessagesMuted( true );

                    player.sendMessage( Utils.getMessage( "messages.muted" ) );
                }
            }
        } );
    }

    protected void disable( final UUID uuid )
    {
        final CompletableFuture<Void> future = CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggleMessagesMuted( uuid, false ) );
        future.thenRun( () ->
        {
            final Player player = Bukkit.getPlayer( uuid );

            if ( player != null && player.isOnline() )
            {
                final User user = (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

                if ( user != null )
                {
                    user.setMessagesMuted( false );

                    player.sendMessage( Utils.getMessage( "messages.unmuted" ) );
                }
            }
        } );
    }

    @Override
    public List<String> getCompletions( final CommandSender sender, final String[] args )
    {
        return ImmutableList.of();
    }

    @Override
    public List<String> getCompletions( Player player, String[] strings )
    {
        return ImmutableList.of();
    }
}
