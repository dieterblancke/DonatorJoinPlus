package com.dbsoftwares.djp.storage.managers;

import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.storage.AbstractStorageManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class FileStorageManager extends AbstractStorageManager
{

    private IConfiguration storage;
    private FileStorageType storageType;
    private File storageFile;

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
                "file-storage." + (storageType.equals( FileStorageType.JSON ) ? "json" : "yml")
        );
        if ( !storageFile.exists() )
        {
            try
            {
                storageFile.createNewFile();
            }
            catch ( IOException e )
            {
                DonatorJoinCore.getInstance().getLog().error( "An error occured: ", e );
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
            DonatorJoinCore.getInstance().getLog().info( "========== STARTING DATA FILE CONVERSION ==========" );
            final File file = new File(
                    DonatorJoinCore.getInstance().getDataFolder(),
                    "file-storage." + (storageType.equals( FileStorageType.JSON ) ? "json" : "yml")
            );
            final File dest = new File(
                    DonatorJoinCore.getInstance().getDataFolder(),
                    "file-storage." + (storageType.equals( FileStorageType.JSON ) ? "json" : "yml") + "-old"
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

            DonatorJoinCore.getInstance().getLog().info( "========== FINISHED DATA FILE CONVERSION ==========" );
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

        return userSection.exists( uuid.toString() ) && userSection.getBoolean( uuid.toString() + ".toggled" );
    }

    @Override
    public void toggle( final UUID uuid, final boolean toggled )
    {
        final ISection userSection = storage.getSection( "users" );

        userSection.set( uuid.toString() + ".toggled", toggled );

        if ( DonatorJoinCore.getInstance().getConfiguration().getBoolean( "storage.save-per-change" ) )
        {
            save();
        }
    }

    @Override
    public String getSlotGroup( final UUID uuid )
    {
        final ISection userSection = storage.getSection( "users" );

        return userSection.exists( uuid.toString() ) ? userSection.getString( uuid.toString() + ".slotgroup" ) : "none";
    }

    @Override
    public void setSlotGroup( final UUID uuid, final String slotGroup )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid.toString() + ".slotgroup", slotGroup );

        if ( DonatorJoinCore.getInstance().getConfiguration().getBoolean( "storage.save-per-change" ) )
        {
            save();
        }
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
}
