package dev.endoy.djp;

import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.api.ISection;
import dev.endoy.djp.storage.AbstractStorageManager;

import java.io.File;
import java.io.InputStream;

public interface DonatorJoinBase
{
    AbstractStorageManager getStorage();

    java.util.logging.Logger getLogger();

    IConfiguration getConfiguration();

    InputStream getResource( String resource );

    File getDataFolder();

    ISection getMessages();

    String color( String s );
}
