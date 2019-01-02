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

import com.dbsoftwares.djp.DonatorJoinPlus;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteStorageManager extends HikariStorageManager {

    public SQLiteStorageManager() {
        super(StorageType.MYSQL, DonatorJoinPlus.i().getConfig().getConfigurationSection("storage"));
        final File database = new File(DonatorJoinPlus.i().getDataFolder(), "sqlite-storage.db");

        try {
            if (!database.exists() && !database.createNewFile()) {
                return;
            }
        } catch (IOException e) {
            DonatorJoinPlus.getLogger().error("An error occured: ", e);
        }

        dataSource.addDataSourceProperty("url", "jdbc:sqlite:" + database.getPath());
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
    }

    @Override
    protected String getDataSourceClass() {
        return "org.sqlite.SQLiteDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}