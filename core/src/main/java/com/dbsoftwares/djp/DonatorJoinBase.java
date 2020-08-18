package com.dbsoftwares.djp;

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.storage.AbstractStorageManager;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
