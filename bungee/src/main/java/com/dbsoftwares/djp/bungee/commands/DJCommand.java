package com.dbsoftwares.djp.bungee.commands;

import com.dbsoftwares.djp.bungee.DonatorJoinPlus;
import com.dbsoftwares.djp.bungee.utils.BungeeUtils;
import com.dbsoftwares.djp.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DJCommand extends Command
{

    public DJCommand()
    {
        super( "donatorjoin", "", "dj", "djp", "donatorjoinplus" );
    }

    @Override
    public void execute( final CommandSender sender, final String[] args )
    {
        if ( args.length == 1 )
        {
            if ( args[0].equalsIgnoreCase( "reload" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.reload" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }
                final DonatorJoinPlus plugin = DonatorJoinPlus.i();

                plugin.loadConfig();

                sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "reloaded" ) ) );
                return;
            }
            else if ( args[0].equalsIgnoreCase( "toggle" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.toggle" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }
                if ( !(sender instanceof ProxiedPlayer) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "not-for-console" ) ) );
                    return;
                }
                final ProxiedPlayer player = (ProxiedPlayer) sender;
                if ( BungeeUtils.get( player ).isToggled() )
                {
                    enable( player.getUniqueId() );
                }
                else
                {
                    disable( player.getUniqueId() );
                }
                return;
            }
            else if ( args[0].equalsIgnoreCase( "enable" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.reload" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }
                if ( !(sender instanceof ProxiedPlayer) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "not-for-console" ) ) );
                    return;
                }
                enable( ((ProxiedPlayer) sender).getUniqueId() );
                return;
            }
            else if ( args[0].equalsIgnoreCase( "disable" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.reload" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }
                if ( !(sender instanceof ProxiedPlayer) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "not-for-console" ) ) );
                    return;
                }

                disable( ((ProxiedPlayer) sender).getUniqueId() );
                return;
            }
        }
        else if ( args.length == 2 )
        {
            if ( args[0].equalsIgnoreCase( "toggle" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.toggle.others" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }

                final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( args[1] );

                if ( player == null )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "not-online" ) ) );
                    return;
                }

                if ( BungeeUtils.get( player ).isToggled() )
                {
                    enable( player.getUniqueId() );
                }
                else
                {
                    disable( player.getUniqueId() );
                }
                return;
            }
            else if ( args[0].equalsIgnoreCase( "enable" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.toggle.others" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }

                final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( args[1] );

                if ( player == null )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "not-online" ) ) );
                    return;
                }

                enable( player.getUniqueId() );
                return;
            }
            else if ( args[0].equalsIgnoreCase( "disable" ) )
            {
                if ( !sender.hasPermission( "donatorjoinplus.toggle.others" ) )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "no-perm" ) ) );
                    return;
                }

                final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( args[1] );

                if ( player == null )
                {
                    sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "not-online" ) ) );
                    return;
                }

                disable( player.getUniqueId() );
                return;
            }
        }

        sender.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "help" ) ) );
    }


    private void enable( final UUID uuid )
    {
        final CompletableFuture<Void> future = CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggle( uuid, false ) );
        future.thenRun( () ->
        {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( uuid );

            if ( player != null && player.isConnected() )
            {
                player.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "enabled" ) ) );
                BungeeUtils.get( player ).setToggled( false );
            }
        } );
    }

    private void disable( final UUID uuid )
    {
        final CompletableFuture<Void> future = CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggle( uuid, true ) );
        future.thenRun( () ->
        {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( uuid );

            if ( player != null && player.isConnected() )
            {
                player.sendMessage( TextComponent.fromLegacyText( Utils.getMessage( "enabled" ) ) );
                BungeeUtils.get( player ).setToggled( true );
            }
        } );
    }
}
