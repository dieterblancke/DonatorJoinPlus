package dev.endoy.djp.storage.managers;

import dev.endoy.configuration.api.FileStorageType;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.api.ISection;
import dev.endoy.djp.DonatorJoinCore;
import dev.endoy.djp.storage.AbstractStorageManager;
import dev.endoy.djp.user.User;

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
    public void setJoinSound( final UUID uuid, final String sound, final Integer volume, final Integer pitch )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid + ".joinsound", sound );
        userSection.set( uuid + ".join_volume", volume );
        userSection.set( uuid + ".join_pitch", pitch );

        checkToSave();
    }

    @Override
    public void setLeaveSound( final UUID uuid, final String sound, final Integer volume, final Integer pitch )
    {
        final ISection userSection = storage.getSection( "users" );
        userSection.set( uuid + ".leavesound", sound );
        userSection.set( uuid + ".leave_volume", volume );
        userSection.set( uuid + ".leave_pitch", pitch );

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
                getOrDefault( section, "join_volume", null ),
                getOrDefault( section, "join_pitch", null ),
                getOrDefault( section, "leavesound", null ),
                getOrDefault( section, "leave_volume", null ),
                getOrDefault( section, "leave_pitch", null ),
                getOrDefault( section, "soundtoggled", false ),
                getOrDefault( section, "fireworktoggled", false ),
                getOrDefault( section, "messagesmuted", false )
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
