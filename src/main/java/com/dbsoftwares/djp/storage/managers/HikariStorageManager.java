/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.djp.storage.managers;

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class HikariStorageManager extends AbstractStorageManager
{

    protected HikariConfig config;
    protected HikariDataSource dataSource;

    @SuppressWarnings("deprecation")
    public HikariStorageManager( final StorageType type, final ISection section )
    {
        super( type );
        config = new HikariConfig();
        config.setDataSourceClassName( getDataSourceClass() );

        // Mysql-only properties
        if ( type == StorageType.MYSQL )
        {
            config.addDataSourceProperty( "serverName", section.getString( "hostname" ) );
            config.addDataSourceProperty( "port", section.getInteger( "port" ) );
            config.addDataSourceProperty( "databaseName", section.getString( "database" ) );
            config.addDataSourceProperty( "user", section.getString( "username" ) );
            config.addDataSourceProperty( "password", section.getString( "password" ) );
            config.addDataSourceProperty( "useSSL", section.getBoolean( "useSSL" ) );

            config.addDataSourceProperty( "cacheServerConfiguration", "true" );
            config.addDataSourceProperty( "elideSetAutoCommits", "true" );
            config.addDataSourceProperty( "useServerPrepStmts", "true" );
            config.addDataSourceProperty( "cacheCallableStmts", "true" );
            config.addDataSourceProperty( "cachePrepStmts", "true" );
            config.addDataSourceProperty( "alwaysSendSetIsolation", "false" );
            config.addDataSourceProperty( "prepStmtCacheSize", "250" );
            config.addDataSourceProperty( "prepStmtCacheSqlLimit", "2048" );
            config.addDataSourceProperty( "useLocalSessionState", "true" );
        }

        config.setMaximumPoolSize( section.getInteger( "pool.max-pool-size" ) );
        config.setMinimumIdle( section.getInteger( "pool.min-idle" ) );
        config.setMaxLifetime( section.getLong( "pool.max-lifetime" ) * 1000 );
        config.setConnectionTimeout( section.getLong( "pool.connection-timeout" ) * 1000 );

        config.setPoolName( "DonatorJoinPlus" );
        config.setLeakDetectionThreshold( 10000 );
        config.setConnectionTestQuery( "/* DonatorJoinPlus ping */ SELECT 1;" );
        config.setInitializationFailTimeout( -1 );
    }

    protected void setupDataSource()
    {
        if ( dataSource == null )
        {
            dataSource = new HikariDataSource( config );
        }
    }

    protected abstract String getDataSourceClass();

    @Override
    public void close()
    {
        dataSource.close();
    }

    private boolean exists( final UUID uuid )
    {
        boolean exists = false;
        try ( Connection connection = getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT toggled FROM djp_data WHERE uuid = ?;" ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                exists = rs.next();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinPlus.getLog().error( "An error occured: ", e );
        }
        return exists;
    }

    @Override
    public boolean isToggled( final UUID uuid )
    {
        boolean toggled = false;
        try ( Connection connection = getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT toggled FROM djp_data WHERE uuid = ? AND toggled = ?;" ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setBoolean( 2, true );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                toggled = rs.next();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinPlus.getLog().error( "An error occured: ", e );
        }
        return toggled;
    }

    @Override
    public void toggle( final UUID uuid, final boolean toggled )
    {
        final boolean exists = exists( uuid );

        try ( Connection connection = getConnection() )
        {
            if ( exists )
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET toggled = ? WHERE uuid = ?;" ) )
                {
                    pstmt.setBoolean( 1, toggled );
                    pstmt.setString( 2, uuid.toString() );

                    pstmt.executeUpdate();
                }
            }
            else
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( "INSERT INTO djp_data(uuid, toggled) VALUES (?, ?);" ) )
                {
                    pstmt.setString( 1, uuid.toString() );
                    pstmt.setBoolean( 2, toggled );

                    pstmt.executeUpdate();
                }
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinPlus.getLog().error( "An error occured: ", e );
        }
    }

    @Override
    public String getSlotGroup( final UUID uuid )
    {
        String slotGroup = "none";
        try ( Connection connection = getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT slotgroup FROM djp_data WHERE uuid = ?;" ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    slotGroup = rs.getString( "slotgroup" );
                }
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinPlus.getLog().error( "An error occured: ", e );
        }
        return slotGroup;
    }

    @Override
    public void setSlotGroup( final UUID uuid, final String slotGroup )
    {
        final boolean exists = exists( uuid );

        try ( Connection connection = getConnection() )
        {
            if ( exists )
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET slotgroup = ? WHERE uuid = ?;" ) )
                {
                    pstmt.setString( 1, slotGroup );
                    pstmt.setString( 2, uuid.toString() );

                    pstmt.executeUpdate();
                }
            }
            else
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( "INSERT INTO djp_data(uuid, slotgroup) VALUES (?, ?);" ) )
                {
                    pstmt.setString( 1, uuid.toString() );
                    pstmt.setString( 2, slotGroup );

                    pstmt.executeUpdate();
                }
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinPlus.getLog().error( "An error occured: ", e );
        }
    }
}
