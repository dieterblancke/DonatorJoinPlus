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

public class JoinSoundSubCommand extends DJSubCommand
{

    public JoinSoundSubCommand()
    {
        super( "joinsound", 1, 2 );
    }

    @Override
    public String getUsage()
    {
        return "/djp joinsound (sound) [player]";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.changesound.join";
    }

    @Override
    public void onExecute( Player player, String[] args )
    {
        if ( args.length > 4 )
        {
            player.sendMessage( Utils.getMessage( "sound.join.usage" ) );
            return;
        }
        if ( args.length == 4 )
        {
            onExecute( (CommandSender) player, args );
            return;
        }
        final String sound = Utils.getFromArrayOrDefault( args, 0, "" );
        final float volume = Utils.getFromArrayOrDefault( args, 1, 20F, Float::parseFloat );
        final float pitch = Utils.getFromArrayOrDefault( args, 2, -20F, Float::parseFloat );
        final User user = DonatorJoinPlus.i().getUser( player.getUniqueId() );

        if ( !validateSound( sound, player ) )
        {
            return;
        }

        DonatorJoinPlus.i().getStorage().setJoinSound( player.getUniqueId(), sound, volume, pitch );
        if ( user != null )
        {
            user.setJoinSound( sound, volume, pitch );
        }
        player.sendMessage(
                Utils.getMessage( "sound.join.changed" )
                        .replace( "{sound}", sound )
                        .replace( "{volume}", String.valueOf( volume ) )
                        .replace( "{volume}", String.valueOf( pitch ) )
        );
    }

    @Override
    public void onExecute( CommandSender sender, String[] args )
    {
        if ( args.length != 2 )
        {
            sender.sendMessage( Utils.getMessage( "sound.join.console-usage" ) );
            return;
        }
        if ( !sender.hasPermission( "donatorjoinplus.changesound.join.other" ) )
        {
            sender.sendMessage( Utils.getMessage( "no-perm" ) );
            return;
        }
        final String sound = Utils.getFromArrayOrDefault( args, 0, "" );
        final float volume = Utils.getFromArrayOrDefault( args, 1, 20F, Float::parseFloat );
        final float pitch = Utils.getFromArrayOrDefault( args, 2, -20F, Float::parseFloat );
        final String playerName = Utils.getFromArrayOrDefault( args, 3, null );

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

        DonatorJoinPlus.i().getStorage().setJoinSound( uuid, sound, volume, pitch );
        if ( target != null )
        {
            final User user = DonatorJoinPlus.i().getUser( uuid );

            if ( user != null )
            {
                user.setJoinSound( sound, volume, pitch );
            }
            target.sendMessage(
                    Utils.getMessage( "sound.join.changed" )
                            .replace( "{sound}", sound )
                            .replace( "{volume}", String.valueOf( volume ) )
                            .replace( "{volume}", String.valueOf( pitch ) )
            );
        }
        sender.sendMessage(
                Utils.getMessage( "sound.join.changed-other" )
                        .replace( "{user}", target.getName() )
                        .replace( "{sound}", sound )
                        .replace( "{volume}", String.valueOf( volume ) )
                        .replace( "{volume}", String.valueOf( pitch ) )
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
