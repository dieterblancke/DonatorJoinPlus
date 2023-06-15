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

import be.dieterblancke.configuration.api.ISection;
import com.dbsoftwares.djp.DonatorJoinCore;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLStorageManager extends HikariStorageManager
{

    public MySQLStorageManager()
    {
        super(
                StorageType.MYSQL,
                DonatorJoinCore.getInstance().getConfiguration().getSection( "storage" ),
                getDefaultHikariConfig()
        );

        setupDataSource();
    }

    private static HikariConfig getDefaultHikariConfig()
    {
        final HikariConfig config = new HikariConfig();
        final ISection section = DonatorJoinCore.getInstance().getConfiguration().getSection( "storage" );

        final String hostname = section.getString( "hostname" );
        final int port = section.getInteger( "port" );
        final String database = section.getString( "database" );

        config.setDriverClassName( "com.mysql.cj.jdbc.Driver" );
        config.setJdbcUrl( "jdbc:mysql://" + hostname + ":" + port + "/" + database );
        config.addDataSourceProperty( "user", section.getString( "username" ) );
        config.addDataSourceProperty( "password", section.getString( "password" ) );
        config.addDataSourceProperty( "cacheServerConfiguration", "true" );
        config.addDataSourceProperty( "elideSetAutoCommits", "true" );
        config.addDataSourceProperty( "useServerPrepStmts", "true" );
        config.addDataSourceProperty( "cacheCallableStmts", "true" );
        config.addDataSourceProperty( "cachePrepStmts", "true" );
        config.addDataSourceProperty( "alwaysSendSetIsolation", "false" );
        config.addDataSourceProperty( "prepStmtCacheSize", "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit", "2048" );
        config.addDataSourceProperty( "useLocalSessionState", "true" );

        return config;
    }

    @Override
    protected String getDataSourceClass()
    {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}