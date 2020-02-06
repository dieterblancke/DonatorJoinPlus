package com.dbsoftwares.djp.spigot.commands.subcommands;

import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import com.dbsoftwares.djp.spigot.commands.DJSubCommand;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.user.User;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class LeaveSoundSubCommand extends DJSubCommand
{

    public LeaveSoundSubCommand()
    {
        super( "leavesound", 1, 2 );
    }

    @Override
    public String getUsage()
    {
        return "/djp leavesound (sound) [player]";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.changesound.leave";
    }

    @Override
    public void onExecute( Player player, String[] args )
    {
        if ( args.length > 2 )
        {
            player.sendMessage( Utils.getMessage( "sound.leave.usage" ) );
            return;
        }
        if ( args.length == 2 )
        {
            onExecute( (CommandSender) player, args );
            return;
        }
        final String sound = args[0];
        final User user = DonatorJoinPlus.i().getUser( player.getUniqueId() );

        if ( !validateSound( sound, player ) )
        {
            return;
        }

        DonatorJoinPlus.i().getStorage().setLeaveSound( player.getUniqueId(), sound );
        if ( user != null )
        {
            user.setLeaveSound( sound );
        }
        player.sendMessage(
                Utils.getMessage( "sound.leave.changed" )
                        .replace( "{sound}", sound )
        );
    }

    @Override
    public void onExecute( CommandSender sender, String[] args )
    {
        if ( args.length != 2 )
        {
            sender.sendMessage( Utils.getMessage( "sound.leave.console-usage" ) );
            return;
        }
        if ( !sender.hasPermission( "donatorjoinplus.changesound.leave.other" ) )
        {
            sender.sendMessage( Utils.getMessage( "sound.no-perm" ) );
            return;
        }
        final String sound = args[0];
        final String playerName = args[1];

        if ( !validateSound( sound, sender ) )
        {
            return;
        }

        final UUID uuid = SpigotUtils.getUuid( args[0] );
        if ( uuid == null )
        {
            sender.sendMessage( Utils.getMessage( "never-joined" ) );
            return;
        }
        final Player target = Bukkit.getPlayer( uuid );

        DonatorJoinPlus.i().getStorage().setLeaveSound( uuid, sound );
        if ( target != null )
        {
            final User user = DonatorJoinPlus.i().getUser( uuid );

            if ( user != null )
            {
                user.setLeaveSound( sound );
            }
            target.sendMessage(
                    Utils.getMessage( "sound.leave.changed" )
                            .replace( "{sound}", sound )
            );
        }
        sender.sendMessage(
                Utils.getMessage( "sound.leave.changed" )
                        .replace( "{user}", target.getName() )
                        .replace( "{sound}", sound )
        );
    }

    @Override
    public List<String> getCompletions( CommandSender sender, String[] args )
    {
        if ( args.length == 1 )
        {
            return getAllowedSounds();
        }
        else if ( args.length == 2 )
        {
            final String lastWord = args[1];
            final List<String> list = Lists.newArrayList();

            for ( String sound : getAllowedSounds() )
            {
                if ( sound.toLowerCase().startsWith( lastWord.toLowerCase() ) )
                {
                    list.add( sound );
                }
            }

            return list;
        }
        else
        {
            return getPlayerCompletions( args );
        }
    }

    @Override
    public List<String> getCompletions( Player player, String[] args )
    {
        return getCompletions( (CommandSender) player, args );
    }
}
