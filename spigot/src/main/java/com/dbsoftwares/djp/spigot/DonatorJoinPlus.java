package com.dbsoftwares.djp.spigot;

/*
 * Created by DBSoftwares on 12 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.commandapi.CommandManager;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinBase;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.library.Library;
import com.dbsoftwares.djp.library.StandardLibrary;
import com.dbsoftwares.djp.spigot.commands.DJCommand;
import com.dbsoftwares.djp.spigot.data.RankData;
import com.dbsoftwares.djp.spigot.listeners.PlayerListener;
import com.dbsoftwares.djp.spigot.listeners.SlotListener;
import com.dbsoftwares.djp.spigot.slots.SlotLimit;
import com.dbsoftwares.djp.spigot.slots.SlotResizer;
import com.dbsoftwares.djp.spigot.utils.SpigotUtils;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.dbsoftwares.djp.storage.managers.FileStorageManager;
import com.dbsoftwares.djp.user.User;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class DonatorJoinPlus extends JavaPlugin implements DonatorJoinBase
{

    private final List<RankData> rankData = Lists.newArrayList();
    private final List<SlotLimit> slotLimits = Lists.newArrayList();
    private Permission permission;
    private boolean disableJoinMessage;
    private boolean disableQuitMessage;
    private boolean usePriorities;
    private boolean usePermissions;
    private AbstractStorageManager storage;
    private IConfiguration configuration;
    private SlotResizer slotResizer;
    private IConfiguration messages;

    public static DonatorJoinPlus i()
    {
        return getPlugin( DonatorJoinPlus.class );
    }

    @Override
    public void onEnable()
    {
        DonatorJoinCore.setInstance( this );

        loadConfig();
        loadMessages();

        // Loading libraries for storage
        for ( StandardLibrary standardLibrary : StandardLibrary.values() )
        {
            final Library library = standardLibrary.getLibrary();

            if ( library.isToLoad() )
            {
                library.load();
            }
        }

        slotResizer = new SlotResizer();
        loadConfig();

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration( Permission.class );
        if ( permissionProvider != null )
        {
            permission = permissionProvider.getProvider();
        }

        getServer().getPluginManager().registerEvents( new PlayerListener(), this );
        getServer().getPluginManager().registerEvents( new SlotListener(), this );
        CommandManager.getInstance().registerCommand( new DJCommand() );

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

            if ( storage instanceof FileStorageManager )
            {
                ( (FileStorageManager) storage ).convert();
            }
        }
        catch ( Exception e )
        {
            getLogger().log( Level.SEVERE, "An error occured", e );
        }

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
        if ( configuration == null )
        {
            final File configFile = new File( getDataFolder(), "config.yml" );

            if ( !configFile.exists() )
            {
                IConfiguration.createDefaultFile( getResource( "config.yml" ), configFile );
            }

            configuration = IConfiguration.loadYamlConfiguration( configFile );
        }
        else
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
        }
        rankData.clear();
        slotLimits.clear();

        if ( configuration.exists( "ranks" ) )
        {
            final List<ISection> ranks = configuration.getSectionList( "ranks" );

            ranks.forEach( section ->
            {
                final RankData data = new RankData();
                data.fromSection( section );

                rankData.add( data );
            } );
            rankData.sort( ( o1, o2 ) -> Integer.compare( o2.getPriority(), o1.getPriority() ) );
        }

        if ( configuration.getBoolean( "slotforcer.enabled" ) )
        {
            SlotLimit.resetCounter();
            configuration.getSectionList( "slotforcer.limits" )
                    .forEach( section -> slotLimits.add( new SlotLimit( section ) ) );

            slotLimits.sort( ( o1, o2 ) -> Integer.compare( o2.getLimit(), o1.getLimit() ) );
        }

        disableJoinMessage = configuration.getBoolean( "joinmessage" );
        disableQuitMessage = configuration.getBoolean( "quitmessage" );
        usePriorities = configuration.getBoolean( "usepriorities" );
        usePermissions = configuration.getBoolean( "usepermissions" );
    }

    public void loadMessages()
    {
        if ( messages == null )
        {

            final File messagesFile = new File( getDataFolder(), "messages.yml" );

            if ( !messagesFile.exists() )
            {
                IConfiguration.createDefaultFile( getResource( "messages.yml" ), messagesFile );

                messages = IConfiguration.loadYamlConfiguration( messagesFile );
            }
            else
            {
                messages = IConfiguration.loadYamlConfiguration( messagesFile );

                // If new messages are added in the plugin, this should automatically load them into the messages file.
                final IConfiguration defaultMessages = IConfiguration.loadYamlConfiguration(
                        getResource( "messages.yml" )
                );

                try
                {
                    messages.copyDefaults( defaultMessages );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try
            {
                messages.reload();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
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

    public User getUser( final UUID uuid )
    {
        final Player player = Bukkit.getPlayer( uuid );

        if ( player == null )
        {
            return null;
        }

        return (User) SpigotUtils.getMetaData( player, SpigotUtils.USER_KEY );
    }

    @Override
    public String color( String s )
    {
        return SpigotUtils.c( s );
    }
}