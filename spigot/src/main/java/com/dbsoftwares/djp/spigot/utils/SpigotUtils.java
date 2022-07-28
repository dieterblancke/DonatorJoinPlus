package com.dbsoftwares.djp.spigot.utils;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.djp.spigot.DonatorJoinPlus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotUtils
{

    public static final String USER_KEY = "DJP_USER";
    private static final Pattern HEX_PATTERN = Pattern.compile( "<#([A-Fa-f0-9]){6}>" );

    private SpigotUtils()
    {
    }

    public static void setMetaData( final Player player, final String key, final Object value )
    {
        // Removing first to be sure
        player.removeMetadata( key, DonatorJoinPlus.i() );

        // Setting meta data
        player.setMetadata( key, new FixedMetadataValue( DonatorJoinPlus.i(), value ) );
    }

    public static <T> T getMetaData( final Player player, final String key )
    {
        return (T) getMetaData( player, key, null );
    }

    public static <T> T getMetaData( final Player player, final String key, T defaultValue )
    {
        if ( player == null )
        {
            return null;
        }
        for ( MetadataValue meta : player.getMetadata( key ) )
        {
            if ( meta.getOwningPlugin().getName().equalsIgnoreCase( DonatorJoinPlus.i().getName() ) )
            {
                return (T) meta.value();
            }
        }
        return defaultValue;
    }

    public static boolean isVanished( Player player )
    {
        for ( MetadataValue meta : player.getMetadata( "vanished" ) )
        {
            if ( meta.asBoolean() )
            {
                return true;
            }
        }
        return false;
    }

    public static void spawnFirework( Location loc )
    {
        Firework firework = loc.getWorld().spawn( loc, Firework.class );

        FireworkMeta fireworkmeta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .withTrail().withFlicker()
                .withFade( Color.GREEN )
                .withColor( Color.WHITE ).withColor( Color.YELLOW )
                .withColor( Color.BLUE ).withColor( Color.FUCHSIA )
                .withColor( Color.PURPLE ).withColor( Color.MAROON )
                .withColor( Color.LIME ).withColor( Color.ORANGE )
                .with( FireworkEffect.Type.BALL_LARGE );

        fireworkmeta.addEffect( builder.build() );
        fireworkmeta.setPower( 1 );
        firework.setFireworkMeta( fireworkmeta );
    }

    public static boolean contains( String[] groups, String group )
    {
        for ( String g : groups )
        {
            if ( g.equalsIgnoreCase( group ) )
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings( "deprecation" )
    public static UUID getUuid( final String name )
    {
        final CompletableFuture<UUID> future = CompletableFuture.supplyAsync( () ->
        {
            OfflinePlayer player = Bukkit.getPlayer( name );

            if ( player == null )
            {
                player = Bukkit.getOfflinePlayer( name );
            }

            return player == null || !player.hasPlayedBefore() ? null : player.getUniqueId();
        } );

        try
        {
            return future.get();
        }
        catch ( InterruptedException | ExecutionException e )
        {
            return null;
        }
    }

    public static String c( final String s )
    {
        if ( ReflectionUtils.getNumericVersion() > 160 )
        {
            return applyColor( s );
        }
        return ChatColor.translateAlternateColorCodes( '&', s );
    }

    private static String applyColor( String message )
    {
        Matcher matcher = HEX_PATTERN.matcher( message );
        while ( matcher.find() )
        {
            final ChatColor hexColor = ChatColor.of( matcher.group().substring( 1, matcher.group().length() - 1 ) );
            final String before = message.substring( 0, matcher.start() );
            final String after = message.substring( matcher.end() );

            message = before + hexColor + after;
            matcher = HEX_PATTERN.matcher( message );
        }
        return ChatColor.translateAlternateColorCodes( '&', message );
    }

    public static String formatString( final Player p, String str )
    {
        if ( str == null || str.isEmpty() )
        {
            return "";
        }
        str = str.replace( "%player%", p.getName() );
        str = str.replace( "{player}", p.getName() );
        str = c( str );

        if ( Bukkit.getPluginManager().isPluginEnabled( "PlaceholderAPI" ) )
        {
            str = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders( (OfflinePlayer) p, str );
        }
        return str;
    }

    public static BaseComponent[] format( final Player player, final List<String> messages )
    {
        final AtomicInteger count = new AtomicInteger();
        return messages
                .stream()
                .map( message ->
                {
                    if ( count.incrementAndGet() >= messages.size() )
                    {
                        return c( formatString( player, message ) );
                    }
                    return c( formatString( player, message + "\n" ) );
                } )
                .map( message -> new BaseComponent[]{ new TextComponent( message ) } )
                .flatMap( Arrays::stream )
                .toArray( BaseComponent[]::new );
    }
}