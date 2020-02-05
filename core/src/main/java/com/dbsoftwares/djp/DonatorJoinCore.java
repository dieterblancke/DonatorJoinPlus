package com.dbsoftwares.djp;

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.djp.storage.AbstractStorageManager;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public interface DonatorJoinCore
{

    DonatorJoinCore instance = null;

    static DonatorJoinCore getInstance()
    {
        return instance;
    }

    static void setInstance( final DonatorJoinCore inst )
    {
        try
        {
            final Field field = DonatorJoinCore.class.getField( "instance" );
            field.setAccessible( true );

            Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL );

            field.set( null, inst );
        }
        catch ( NoSuchFieldException | IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }

    AbstractStorageManager getStorage();

    java.util.logging.Logger getLogger();

    IConfiguration getConfiguration();

    InputStream getResource( String resource );

    File getDataFolder();
}
