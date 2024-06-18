package dev.endoy.djp.spigot.slots;

import dev.endoy.djp.spigot.utils.ReflectionUtils;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlotResizer
{

    @Getter
    private final int max;
    private final Map<UUID, SlotLimit> playerLimits = Collections.synchronizedMap( new HashMap<>() );
    private final Map<SlotLimit, Integer> slotLimits = Collections.synchronizedMap( new HashMap<>() );

    public SlotResizer()
    {
        // max players on initialize, we're manipulating this value, so making sure we have the original stored
        this.max = Bukkit.getMaxPlayers();
    }

    public boolean isPlaceAvailable( final SlotLimit limit )
    {
        final int currentlyUsed = slotLimits.getOrDefault( limit, 0 );

        return currentlyUsed < limit.getLimit();
    }

    public synchronized boolean grantSlot( final UUID uuid, final SlotLimit limit )
    {
        if ( !isPlaceAvailable( limit ) )
        {
            return false;
        }

        playerLimits.put( uuid, limit );
        slotLimits.put( limit, slotLimits.getOrDefault( limit, 0 ) + 1 );

        return resize();
    }

    public synchronized void removePlayer( final UUID uuid )
    {
        if ( !playerLimits.containsKey( uuid ) )
        {
            return;
        }
        final SlotLimit limit = playerLimits.remove( uuid );
        final int currentlyUsed = slotLimits.getOrDefault( limit, 0 ) - 1;

        if ( currentlyUsed > 0 )
        {
            slotLimits.put( limit, currentlyUsed );
        }
        else
        {
            slotLimits.remove( limit );
        }

        resize();
    }

    public boolean isPlaceAvailable()
    {
        return getOnlinePlayers() < max;
    }

    private int calculateMax()
    {
        int additional = slotLimits.values().stream().mapToInt( Integer::intValue ).sum();

        return max + additional;
    }

    private boolean resize()
    {
        int max = calculateMax();
        Object playerList = ReflectionUtils.getHandle( Bukkit.getServer().getClass(), Bukkit.getServer() );
        Field maxPlayers = ReflectionUtils.getField( playerList.getClass().getSuperclass(), "maxPlayers" );

        try
        {
            maxPlayers.set( playerList, max );

            return true;
        }
        catch ( IllegalAccessException e )
        {
            return false;
        }
    }

    private int getOnlinePlayers()
    {
        // If there were to be a memory leak that keeps offline players loaded, it should get skipped here
        return (int) Bukkit.getOnlinePlayers().stream().filter( p -> p != null && p.isOnline() ).count();
    }
}
