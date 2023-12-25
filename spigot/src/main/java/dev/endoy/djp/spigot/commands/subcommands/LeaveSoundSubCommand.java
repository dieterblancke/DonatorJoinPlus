package dev.endoy.djp.spigot.commands.subcommands;

import com.google.common.collect.Lists;
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
        if ( args.length > 4 )
        {
            player.sendMessage( Utils.getMessage( "sound.leave.usage" ) );
            return;
        }
        if ( args.length == 4 )
        {
            onExecute( (CommandSender) player, args );
            return;
        }
        String sound = Utils.getFromArrayOrDefault( args, 0, "" );
        float volume = Utils.getFromArrayOrDefault( args, 1, 20F, Float::parseFloat );
        float pitch = Utils.getFromArrayOrDefault( args, 2, -20F, Float::parseFloat );
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        if ( !validateSound( sound, player ) )
        {
            return;
        }

        DonatorJoinPlus.i().getStorage().setLeaveSound( player.getUniqueId(), sound, volume, pitch );
        if ( user != null )
        {
            user.setLeaveSound( sound );
        }
        player.sendMessage(
                Utils.getMessage( "sound.leave.changed" )
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
            sender.sendMessage( Utils.getMessage( "sound.leave.console-usage" ) );
            return;
        }
        if ( !sender.hasPermission( "donatorjoinplus.changesound.leave.other" ) )
        {
            sender.sendMessage( Utils.getMessage( "no-perm" ) );
            return;
        }
        String sound = Utils.getFromArrayOrDefault( args, 0, "" );
        float volume = Utils.getFromArrayOrDefault( args, 1, 20F, Float::parseFloat );
        float pitch = Utils.getFromArrayOrDefault( args, 2, -20F, Float::parseFloat );
        String playerName = Utils.getFromArrayOrDefault( args, 3, null );

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

        DonatorJoinPlus.i().getStorage().setLeaveSound( uuid, sound, volume, pitch );
        if ( target != null )
        {
            User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( target.getUniqueId() );

            if ( user != null )
            {
                user.setLeaveSound( sound, volume, pitch );
            }
            target.sendMessage(
                    Utils.getMessage( "sound.leave.changed" )
                            .replace( "{sound}", sound )
                            .replace( "{volume}", String.valueOf( volume ) )
                            .replace( "{volume}", String.valueOf( pitch ) )
            );
        }
        sender.sendMessage(
                Utils.getMessage( "sound.leave.changed-other" )
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
