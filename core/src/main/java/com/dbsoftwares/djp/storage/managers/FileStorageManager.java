package com.dbsoftwares.djp.storage.managers;

import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.dbsoftwares.djp.user.User;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FileStorageManager extends AbstractStorageManager
{

    private final File storageFile;
    private IConfiguration storage;
    private FileStorageType storageType;

    public FileStorageManager( final String type )
    {
        super( StorageType.FILE );

        // load configuration file
        try
        {
            storageType = FileStorageType.valueOf( type );
        }
        catch ( IllegalArgumentException e )
        {
            storageType = FileStorageType.JSON;
        }
        storageFile = new File(
                DonatorJoinCore.getInstance().getDataFolder(),
                "file-storage." + ( storageType.equals( FileStorageType.JSON ) ? "json" : "yml" )
        );
        if ( !storageFile.exists() )
        {
            try
            {
                storageFile.createNewFile();
            }
            catch ( IOException e )
            {
                DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }
        this.storage = IConfiguration.loadConfiguration( storageType, storageFile );
        if ( !storage.exists( "users" ) )
        {
            storage.createSection( "users" );
            save();
        }
    }

    public void convert() throws IOException
    {
        if ( storage.exists( "toggled" ) )
        {
            // starting converter ...
            DonatorJoinCore.getInstance().getLogger().info( "========== STARTING DATA FILE CONVERSION ==========" );
            final File file = new File(
                    DonatorJoinCore.getInstance().getDataFolder(),
                    "file-storage." + ( storageType.equals( FileStorageType.JSON ) ? "json" : "yml" )
            );
            final File dest = new File(
                    DonatorJoinCore.getInstance().getDataFolder(),
                    "file-storage." + ( storageType.equals( FileStorageType.JSON ) ? "json" : "yml" ) + "-old"
            );

            storageFile.renameTo( dest );
            file.createNewFile();

            final IConfiguration newStorage = IConfiguration.loadConfiguration( storageType, storageFile );
            final List<String> toggledUsers = storage.getStringList( "toggled" );

            toggledUsers.forEach( user -> newStorage.set( "users." + user + ".toggled", true ) );

            for ( String user : storage.getKeys( false ) )
            {
                try
                {
                    final UUID uuid = UUID.fromString( user );

                    newStorage.set( "users." + user + ".slotgroup", storage.getString( user ) );
                }
                catch ( Exception e )
                {
                    // ignore
                }
            }

            newStorage.save();
            storage = newStorage;

            DonatorJoinCore.getInstance().getLogger().info( "========== FINISHED DATA FILE CONVERSION ==========" );
        }
    }

    @Override
    public boolean exists( UUID uuid )
    {
        return storage.exists( "users" ) && storage.exists( "users." + uuid.toString() );
    }

    @Override
    public boolean isToggled( final UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        return userSection.exists( uuid.toString() ) && userSection.getBoolean( uuid + ".toggled" );
    }

    @Override
    public void toggle( final UUID uuid, final boolean toggled )
    {
        final ISection userSection = storage.getSection( "users" );

        userSection.set( uuid.toString() + ".toggled", toggled );

        checkToSave();
    }

    @Override
    public String getSlotGroup( final UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        return userSection.exists( uuid.toString() ) ? userSection.getString( uuid + ".slotgroup" ) : "none";
    }

    @Override
    public void setSlotGroup( final UUID uuid, final String slotGroup )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".slotgroup", slotGroup );

        checkToSave();
    }

    @Override
    public void setJoinSound( final UUID uuid, final String sound )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".joinsound", sound );

        checkToSave();
    }

    @Override
    public void setLeaveSound( final UUID uuid, final String sound )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".leavesound", sound );

        checkToSave();
    }

    @Override
    public void toggleSound( UUID uuid, boolean toggled )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".soundtoggled", toggled );

        checkToSave();
    }

    @Override
    public void toggleFirework( UUID uuid, boolean toggled )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".fireworktoggled", toggled );

        checkToSave();
    }

    @Override
    public void toggleMessagesMuted( UUID uuid, boolean toggled )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".messagesmuted", toggled );

        checkToSave();
    }

    @Override
    public boolean isSoundToggled( UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        return userSection.exists( uuid.toString() ) && userSection.getBoolean( uuid + ".soundtoggled" );
    }

    @Override
    public boolean isFireworkToggled( UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        return userSection.exists( uuid.toString() ) && userSection.getBoolean( uuid + ".fireworktoggled" );
    }

    @Override
    public boolean isMessagesMuted( UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        return userSection.exists( uuid.toString() ) && userSection.getBoolean( uuid + ".messagesmuted" );
    }

    @Override
    public User getUser( final UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        if ( !userSection.exists( uuid.toString() ) )
        {
            return new User( uuid );
        }
        final ISection section = userSection.getSection( uuid.toString() );

        return new User(
                uuid,
                getOrDefault( section, "toggled", false ),
                getOrDefault( section, "slotgroup", "none" ),
                getOrDefault( section, "joinsound", null ),
                getOrDefault( section, "leavesound", null ),
                getOrDefault( section, "soundtoggled", false ),
                getOrDefault( section, "fireworktoggled", false ),
                getOrDefault( section, "messagesmtued", false )
        );
    }

    private <T> T getOrDefault( final ISection section, final String path, final T def )
    {
        if ( !section.exists( path ) )
        {
            return def;
        }
        return section.get( path );
    }

    @Override
    public Connection getConnection()
    {
        return null;
    }

    @Override
    public void close()
    {
        save();
    }

    private void save()
    {
        try
        {
            storage.save();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private void checkToSave()
    {
        if ( DonatorJoinCore.getInstance().getConfiguration().getBoolean( "storage.save-per-change" ) )
        {
            save();
        }
    }
}
