package dev.endoy.djp.spigot;

/*
 * Created by DBSoftwares on 12 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.api.ISection;
import dev.endoy.spigot.commandapi.CommandManager;
import dev.endoy.djp.DonatorJoinBase;
import dev.endoy.djp.DonatorJoinCore;
import dev.endoy.djp.library.Library;
import dev.endoy.djp.library.StandardLibrary;
import dev.endoy.djp.spigot.commands.DJCommand;
import dev.endoy.djp.spigot.data.RankData;
import dev.endoy.djp.spigot.integrations.vanish.*;
import dev.endoy.djp.spigot.listeners.PlayerListener;
import dev.endoy.djp.spigot.listeners.SlotListener;
import dev.endoy.djp.spigot.manager.UserManager;
import dev.endoy.djp.spigot.slots.SlotLimit;
import dev.endoy.djp.spigot.slots.SlotResizer;
import dev.endoy.djp.spigot.utils.SpigotUtils;
import dev.endoy.djp.storage.AbstractStorageManager;
import dev.endoy.djp.storage.managers.FileStorageManager;
import dev.endoy.djp.utils.Utils;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
    private VanishIntegration vanishIntegration;
    private UserManager userManager;

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
        if ( Utils.getJavaVersion() < 16 )
        {
            for ( StandardLibrary standardLibrary : StandardLibrary.values() )
            {
                final Library library = standardLibrary.getLibrary();

                if ( library.isToLoad() )
                {
                    library.load();
                }
            }
        }

        this.userManager = new UserManager();
        this.slotResizer = new SlotResizer();
        this.vanishIntegration = this.detectVanishIntegration();
        this.vanishIntegration.register();

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

    private VanishIntegration detectVanishIntegration()
    {
        if ( configuration.exists( "vanish-support" ) && configuration.getBoolean( "vanish-support" ) )
        {
            if ( getServer().getPluginManager().isPluginEnabled( "SuperVanish" )
                    || getServer().getPluginManager().isPluginEnabled( "PremiumVanish" ) )
            {
                return new SuperAndPremiumVanishIntegration();
            }
            else if ( getServer().getPluginManager().isPluginEnabled( "VelocityVanish" ) )
            {
                return new VelocityVanishIntegration();
            }
            else if ( getServer().getPluginManager().isPluginEnabled( "Essentials" ) )
            {
                return new EssentialsVanishIntegration();
            }
        }

        return new NoOpVanishIntegration();
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

    @Override
    public String color( String s )
    {
        return SpigotUtils.c( s );
    }
}