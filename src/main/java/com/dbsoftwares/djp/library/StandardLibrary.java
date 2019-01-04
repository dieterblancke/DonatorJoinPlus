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

package com.dbsoftwares.djp.library;

import com.dbsoftwares.djp.DonatorJoinPlus;
import lombok.Getter;

public enum StandardLibrary {

    SQLITE(
            "org.sqlite.JDBC",
            "http://central.maven.org/maven2/org/xerial/sqlite-jdbc/{version}/sqlite-jdbc-{version}.jar",
            "3.25.2",
            checkType("SQLITE")
    ),
    H2(
            "org.h2.jdbcx.JdbcDataSource",
            "http://central.maven.org/maven2/com/h2database/h2/{version}/h2-{version}.jar",
            "1.4.197",
            checkType("H2")
    ),
    HIKARIDB(
            "com.zaxxer.hikari.HikariDataSource",
            "http://central.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "3.2.0",
            checkType("MYSQL", "SQLITE", "H2")
    );

    @Getter
    private final Library library;

    StandardLibrary(String className, String downloadURL, String version, boolean load) {
        this.library = new Library(toString(), className, downloadURL, version, load);
    }

    private static boolean checkType(String... types) {
        for (String type : types) {
            final String storageType = DonatorJoinPlus.i().getConfig().getString("storage.type");

            if (storageType.equalsIgnoreCase(type)) {
                return true;
            } else {
                if (storageType.contains(":") && storageType.split(":")[0].equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}