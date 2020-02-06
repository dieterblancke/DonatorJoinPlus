package com.dbsoftwares.djp.storage;


import com.dbsoftwares.djp.DonatorJoinCore;
import com.dbsoftwares.djp.storage.managers.FileStorageManager;
import com.dbsoftwares.djp.storage.managers.H2StorageManager;
import com.dbsoftwares.djp.storage.managers.MySQLStorageManager;
import com.dbsoftwares.djp.storage.managers.SQLiteStorageManager;
import com.dbsoftwares.djp.user.User;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public abstract class AbstractStorageManager
{

    @Getter
    private static AbstractStorageManager manager;

    @Getter
    private StorageType type;

    public AbstractStorageManager( final StorageType type )
    {
        manager = this;

        this.type = type;
    }

    public String getName()
    {
        return type.getName();
    }

    public void initializeStorage() throws Exception
    {
        if ( !type.hasSchema() )
        {
            return;
        }
        try ( InputStream is = DonatorJoinCore.getInstance().getResource( type.getSchema() ) )
        {
            if ( is == null )
            {
                throw new Exception( "Could not find schema for " + type.toString() + ": " + type.getSchema() + "!" );
            }
            try ( BufferedReader reader = new BufferedReader( new InputStreamReader( is, StandardCharsets.UTF_8 ) );
                  Connection connection = getConnection(); Statement st = connection.createStatement() )
            {

                StringBuilder builder = new StringBuilder();
                String line;
                while ( (line = reader.readLine()) != null )
                {
                    builder.append( line );

                    if ( line.endsWith( ";" ) )
                    {
                        builder.deleteCharAt( builder.length() - 1 );

                        final String statement = builder.toString().trim();
                        if ( !statement.isEmpty() )
                        {
                            st.executeUpdate( statement );
                        }

                        builder = new StringBuilder();
                    }
                }
            }
        }
    }

    public abstract boolean exists( final UUID uuid );

    public abstract boolean isToggled( final UUID uuid );

    public abstract void toggle( final UUID uuid, final boolean toggled );

    public abstract String getSlotGroup( final UUID uuid );

    public abstract void setSlotGroup( final UUID uuid, final String slotGroup );

    public abstract void setJoinSound( final UUID uuid, final String sound );

    public abstract void setLeaveSound( final UUID uuid, final String sound );

    public abstract void toggleSound( final UUID uuid, final boolean toggled );

    public abstract void toggleFirework( final UUID uuid, final boolean toggled );

    public abstract boolean isSoundToggled( final UUID uuid );

    public abstract boolean isFireworkToggled( final UUID uuid );

    public abstract User getUser( final UUID uuid );

    public abstract Connection getConnection() throws SQLException;

    public abstract void close() throws SQLException;

    @Getter
    public enum StorageType
    {

        MYSQL( MySQLStorageManager.class, "MySQL", "schemas/mysql.sql" ),
        SQLITE( SQLiteStorageManager.class, "SQLite", "schemas/sqlite.sql" ),
        H2( H2StorageManager.class, "H2", "schemas/h2.sql" ),
        FILE( FileStorageManager.class, "PLAIN", null );

        private Class<? extends AbstractStorageManager> manager;
        private String name;
        private String schema;

        StorageType( final Class<? extends AbstractStorageManager> manager, final String name, final String schema )
        {
            this.manager = manager;
            this.name = name;
            this.schema = schema;
        }

        public boolean hasSchema()
        {
            return schema != null;
        }
    }
}