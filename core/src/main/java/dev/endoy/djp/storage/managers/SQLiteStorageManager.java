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

package dev.endoy.djp.storage.managers;

import dev.endoy.djp.DonatorJoinCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteStorageManager extends HikariStorageManager
{

    public SQLiteStorageManager()
    {
        super( StorageType.SQLITE, DonatorJoinCore.getInstance().getConfiguration().getSection( "storage" ) );
        final File database = new File( DonatorJoinCore.getInstance().getDataFolder(), "sqlite-storage.db" );

        try
        {
            if ( !database.exists() && !database.createNewFile() )
            {
                return;
            }
        }
        catch ( IOException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        config.addDataSourceProperty( "url", "jdbc:sqlite:" + database.getPath() );
        setupDataSource();
    }

    @Override
    protected String getDataSourceClass()
    {
        return "org.sqlite.SQLiteDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}