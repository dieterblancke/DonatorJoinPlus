package com.dbsoftwares.djp.spigot.commands.subcommands;

import com.dbsoftwares.commandapi.command.SubCommand;
import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SetSlotGroupSubCommand extends SubCommand
{

    public SetSlotGroupSubCommand()
    {
        super( "setslotgroup", 2 );
    }

    @Override
    public String getUsage()
    {
        return "/djp setslotgroup (player) (groupname / none)";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.setslotgroup";
    }

    @Override
    public void onExecute( final Player player, final String[] args )
    {
        onExecute( (CommandSender) player, args );
    }

    @Override
    public void onExecute( final CommandSender sender, final String[] args )
    {
        final String playerName = args[0];
        final String groupName = args[1];
        final UUID uuid = SpigotUtils.getUuid( playerName );

        if ( uuid == null )
        {
            sender.sendMessage( Utils.getMessage( "never-joined" ) );
            return;
        }
        if ( !groupName.equalsIgnoreCase( "none" )
                && DonatorJoinPlus.i().getSlotLimits().stream().noneMatch( l -> l.getName().equalsIgnoreCase( groupName ) ) )
        {
            sender.sendMessage( Utils.getMessage( "group-not-found" ).replace( "{group}", groupName ) );
            return;
        }

        try
        {
            DonatorJoinPlus.i().getStorage().setSlotGroup( uuid, groupName );
            sender.sendMessage( Utils.getMessage( "group-set" ).replace( "{player}", playerName ).replace( "{group}", groupName ) );
        } catch ( Exception e )
        {
            e.printStackTrace();
            sender.sendMessage( Utils.getMessage( "error" ) );
        }
    }

    @Override
    public List<String> getCompletions( final CommandSender sender, final String[] args )
    {
        return null;
    }

    @Override
    public List<String> getCompletions( final Player player, final String[] args )
    {
        return null;
    }
}
