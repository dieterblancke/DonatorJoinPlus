package com.dbsoftwares.djp.bungee.listeners;

import com.dbsoftwares.djp.bungee.DonatorJoinPlus;
import com.dbsoftwares.djp.bungee.data.EventData;
import com.dbsoftwares.djp.bungee.data.RankData;
import com.dbsoftwares.djp.bungee.utils.BungeeUtils;
import com.dbsoftwares.djp.bungee.utils.MessageBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class DJPListener
{

    protected void execute( final ProxiedPlayer player, final EventData.EventType type )
    {
        for ( RankData data : DonatorJoinPlus.i().getRankData() )
        {
            if ( player.hasPermission( data.getPermission() ) )
            {
                final EventData eventData = data.getEvents().get( type );

                if ( eventData == null )
                {
                    continue;
                }

                DonatorJoinPlus.i().debug( "Player " + player.getName() + " has the permission " + data.getPermission() + ", executing event ..." );

                executeEventData( player, eventData );

                if ( DonatorJoinPlus.i().getConfiguration().getBoolean( "usepriorities" ) )
                {
                    break;
                }
            }
        }
    }

    protected void executeEventData( final ProxiedPlayer p, final EventData eventData )
    {
        if ( eventData != null && eventData.isEnabled() )
        {
            final TextComponent textComponent = MessageBuilder.buildMessage( p, eventData.getMessage() );

            ProxyServer.getInstance().broadcast( textComponent );

            if ( eventData.isCommandsEnabled() && eventData.getCommands() != null && !eventData.getCommands().isEmpty() )
            {
                for ( String command : eventData.getCommands() )
                {
                    command = BungeeUtils.formatString( p, command );

                    DonatorJoinPlus.i().debug( "Executing command " + command + " for player " + p.getName() + "." );

                    if ( command.startsWith( "player:" ) )
                    {
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(
                                p,
                                command.replaceFirst( "player:", "" )
                        );
                    }
                    else
                    {
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(
                                ProxyServer.getInstance().getConsole(),
                                command
                        );
                    }
                }
            }
        }
    }
}
