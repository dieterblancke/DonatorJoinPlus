package dev.endoy.djp.spigot.commands;

import dev.endoy.spigot.commandapi.command.SubCommand;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.XSound;
import dev.endoy.djp.utils.Utils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public abstract class DJSubCommand extends SubCommand
{

    public DJSubCommand( String name )
    {
        super( name );
    }

    public DJSubCommand( String name, int minimumArgs )
    {
        super( name, minimumArgs );
    }

    public DJSubCommand( String name, int minimumArgs, int maximumArgs )
    {
        super( name, minimumArgs, maximumArgs );
    }

    protected List<String> getPlayerCompletions( final String[] args )
    {
        if ( args.length > 0 )
        {
            final String lastWord = args[args.length - 1];
            final List<String> list = Lists.newArrayList();

            for ( Player p : Bukkit.getOnlinePlayers() )
            {
                if ( StringUtil.startsWithIgnoreCase( p.getName(), lastWord ) )
                {
                    list.add( p.getName() );
                }
            }

            return list;
        }
        else
        {
            final List<String> list = Lists.newArrayList();

            for ( Player p : Bukkit.getOnlinePlayers() )
            {
                list.add( p.getName() );
            }

            return list;
        }
    }

    protected boolean validateSound( final String sound, final CommandSender sender )
    {
        Optional<XSound> optionalXSound = ofNullable( Strings.emptyToNull( sound ) ).flatMap( XSound::matchXSound );
        if ( optionalXSound.isEmpty() || !optionalXSound.get().isSupported() )
        {
            sender.sendMessage(
                    Utils.getMessage( "sound.invalid-sound" )
            );
            return false;
        }

        if ( !isAllowedSound( sound ) )
        {
            sender.sendMessage(
                    Utils.getMessage( "sound.unallowed-sound" )
            );
            return false;
        }
        return true;
    }

    protected List<String> getAllowedSounds()
    {
        return Arrays.stream( XSound.values() )
                .filter( XSound::isSupported )
                .map( XSound::name )
                .filter( this::isAllowedSound )
                .collect( Collectors.toList() );
    }

    private boolean isAllowedSound( final String sound )
    {
        final List<String> sounds = DonatorJoinPlus.i().getConfiguration().getStringList( "sounds.list" );

        if ( DonatorJoinPlus.i().getConfiguration().getString( "sounds.mode" ).equalsIgnoreCase( "WHITELIST" ) )
        {
            return sounds.stream().anyMatch( s -> s.equalsIgnoreCase( sound ) );
        }
        else
        {
            return sounds.stream().noneMatch( s -> s.equalsIgnoreCase( sound ) );
        }
    }
}
