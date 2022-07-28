package com.dbsoftwares.djp.bungee;

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinBase;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.bungee.commands.DJCommand;
import com.dbsoftwares.djp.bungee.data.RankData;
import com.dbsoftwares.djp.bungee.listeners.PlayerListener;
import com.dbsoftwares.djp.bungee.listeners.VanishListener;
import com.dbsoftwares.djp.bungee.utils.BungeeUtils;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bungeecord.Metrics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

@Getter
public class DonatorJoinPlus extends Plugin implements DonatorJoinBase
{

    private final List<RankData> rankData = Lists.newArrayList();
    private Permission permission;
    private boolean usePriorities;
    private AbstractStorageManager storage;
    private IConfiguration configuration;
    private ISection messages;

    public static DonatorJoinPlus i()
    {
        return (DonatorJoinPlus) DonatorJoinCore.getInstance();
    }

    @Override
    public void onEnable()
    {
        DonatorJoinCore.setInstance( this );

        final File configFile = new File( getDataFolder(), "config.yml" );

        if ( !configFile.exists() )
        {
            IConfiguration.createDefaultFile( getResource( "config.yml" ), configFile );
        }

        configuration = IConfiguration.loadYamlConfiguration( configFile );
        messages = configuration.getSection( "messages" );

        loadConfig();

        AbstractStorageManager.StorageType type;
        final String typeString = configuration.getString( "storage.type" ).toUpperCase();
        try
        {

            if ( typeString.contains( ":" ) )
            {
                type = AbstractStorageManager.StorageType.valueOf( typeString.split( ":" )[0] );
            }
            else
            {
                type = AbstractStorageManager.StorageType.valueOf( typeString );
            }
        }
        catch ( IllegalArgumentException e )
        {
            type = AbstractStorageManager.StorageType.MYSQL;
        }
        try
        {
            storage = typeString.contains( ":" )
                    ? type.getManager().getConstructor( String.class ).newInstance( typeString.split( ":" )[1] )
                    : type.getManager().getConstructor().newInstance();
            storage.initializeStorage();
        }
        catch ( Exception e )
        {
            getLogger().log( Level.SEVERE, "An error occured", e );
        }

        this.getProxy().getPluginManager().registerListener( this, new PlayerListener() );

        if ( ProxyServer.getInstance().getPluginManager().getPlugin( "PremiumVanish" ) != null )
        {
            this.getProxy().getPluginManager().registerListener( this, new VanishListener() );
        }

        this.getProxy().getPluginManager().registerCommand( this, new DJCommand() );

        new Metrics( this );
    }

    @Override
    public void onDisable()
    {
        try
        {
            storage.close();
        }
        catch ( SQLException e )
        {
            getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    public void loadConfig()
    {
        try
        {
            configuration.reload();
        }
        catch ( IOException e )
        {
            getLogger().log( Level.SEVERE, "An error occured", e );
            return;
        }
        rankData.clear();

        final List<ISection> ranks = configuration.getSectionList( "ranks" );

        ranks.forEach( section ->
        {
            final RankData data = new RankData();
            data.fromSection( section );

            rankData.add( data );
        } );
        rankData.sort( ( o1, o2 ) -> Integer.compare( o2.getPriority(), o1.getPriority() ) );

        usePriorities = configuration.getBoolean( "usepriorities" );
    }

    public boolean isDebugMode()
    {
        return configuration.exists( "debug" ) && configuration.getBoolean( "debug" );
    }

    public void debug( final String message )
    {
        if ( isDebugMode() )
        {
            getLogger().info( message );
        }
    }

    @Override
    public InputStream getResource( String resource )
    {
        return getResourceAsStream( resource );
    }

    @Override
    public String color( String s )
    {
        return BungeeUtils.c( s );
    }
}
