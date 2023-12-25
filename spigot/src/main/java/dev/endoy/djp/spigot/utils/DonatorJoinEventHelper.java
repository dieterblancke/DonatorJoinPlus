package dev.endoy.djp.spigot.utils;

import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.data.EventData;
import dev.endoy.djp.spigot.data.RankData;
import dev.endoy.djp.spigot.data.WorldEventData;
import dev.endoy.djp.user.User;
import com.google.common.base.Strings;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class DonatorJoinEventHelper
{

    private DonatorJoinEventHelper()
    {
    }

    public static void executeEvent( final User user, final boolean join, final World world, final Player p )
    {
        final String[] groups = DonatorJoinPlus.i().getPermission().getPlayerGroups( p );

        DonatorJoinPlus.i().debug( "List of groups for player " + p.getName() + ": " + Arrays.toString( groups ) );

        for ( RankData data : DonatorJoinPlus.i().getRankData() )
        {
            final EventData.EventType type = join ? EventData.EventType.JOIN : EventData.EventType.QUIT;
            final EventData eventData = ( world != null ? data.getWorldEvents() : data.getEvents() ).getOrDefault( type, null );

            if ( eventData == null )
            {
                continue;
            }

            if ( DonatorJoinPlus.i().isUsePermissions() )
            {
                if ( DonatorJoinPlus.i().getPermission().has( p, data.getPermission() ) )
                {
                    DonatorJoinPlus.i().debug( "Player " + p.getName() + " has the permission " + data.getPermission() + ", executing event ..." );

                    executeEventData( user, p, eventData, world, eventData.getDelay() );

                    if ( DonatorJoinPlus.i().getConfiguration().getBoolean( "usepriorities" ) )
                    {
                        break;
                    }
                }
                else
                {
                    DonatorJoinPlus.i().debug( "Player " + p.getName() + " does not have the permission " + data.getPermission() + "." );
                }
            }
            else
            {
                if ( SpigotUtils.contains( groups, data.getName() ) )
                {
                    DonatorJoinPlus.i().debug( "Player " + p.getName() + " is in the group " + data.getName() + ", executing event ..." );

                    executeEventData( user, p, eventData, world, eventData.getDelay() );

                    if ( DonatorJoinPlus.i().getConfiguration().getBoolean( "usepriorities" ) )
                    {
                        break;
                    }
                }
            }
        }
    }

    private static void executeEventData( final User user, final Player p, final EventData eventData, final World world, final long delay )
    {
        if ( delay > 0 )
        {
            Bukkit.getScheduler().runTaskLater( DonatorJoinPlus.i(), () -> executeEventData( user, p, eventData, world ), delay );
        }
        else
        {
            executeEventData( user, p, eventData, world );
        }
    }

    private static void executeEventData( final User user, final Player p, final EventData eventData, final World world )
    {
        if ( eventData instanceof WorldEventData && world != null && ( (WorldEventData) eventData ).ckeckWorld( world.getName() ) )
        {
            return;
        }

        if ( eventData.isEnabled() )
        {
            final TextComponent textComponent = MessageBuilder.buildMessage( p, eventData.getMessage() );

            if ( textComponent != null )
            {
                if ( world != null )
                {
                    for ( Player player : world.getPlayers() )
                    {
                        player.spigot().sendMessage( textComponent );
                    }
                    Bukkit.getConsoleSender().spigot().sendMessage( textComponent );
                }
                else
                {
                    broadcast( textComponent );
                }
            }

            if ( eventData.isFirework() && !user.isFireworkToggled() )
            {
                SpigotUtils.spawnFirework( p.getLocation() );
            }

            if ( eventData.isSoundEnabled() && ( user == null || !user.isSoundToggled() ) )
            {
                String soundName = user == null ? null : ( eventData.getType() == EventData.EventType.JOIN ? user.getJoinSound() : user.getLeaveSound() );
                Optional<XSound> optionalXSound = ofNullable( Strings.emptyToNull( soundName ) ).flatMap( XSound::matchXSound );

                float soundVolume = user == null ? eventData.getSoundVolume() : ( eventData.getType() == EventData.EventType.JOIN ? user.getJoinSoundVolume() : user.getLeaveSoundVolume() );
                float soundPitch = user == null ? eventData.getSoundPitch() : ( eventData.getType() == EventData.EventType.JOIN ? user.getJoinSoundPitch() : user.getLeaveSoundPitch() );

                if ( soundName != null && optionalXSound.isPresent() )
                {
                    XSound sound = optionalXSound.get();

                    sound.play( p.getLocation(), soundVolume, soundPitch );
                }
                else if ( eventData.getSound() != null )
                {
                    XSound sound = XSound.matchXSound( eventData.getSound() );

                    sound.play( p.getLocation(), soundVolume, soundPitch );
                }
            }

            if ( eventData.isCommandsEnabled() && eventData.getCommands() != null && !eventData.getCommands().isEmpty() )
            {
                for ( String command : eventData.getCommands() )
                {
                    command = SpigotUtils.formatString( p, command );

                    DonatorJoinPlus.i().debug( "Executing command " + command + " for player " + p.getName() + "." );

                    if ( command.startsWith( "player:" ) )
                    {
                        p.performCommand( command.replaceFirst( "player:", "" ) );
                    }
                    else
                    {
                        Bukkit.dispatchCommand( Bukkit.getConsoleSender(), command );
                    }
                }
            }
        }
    }

    public static void broadcast( final TextComponent component )
    {
        for ( Player player : Bukkit.getOnlinePlayers() )
        {
            final User user = SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY, null );

            if ( user == null || !user.isMessagesMuted() )
            {
                player.spigot().sendMessage( component );
            }
        }
    }
}
