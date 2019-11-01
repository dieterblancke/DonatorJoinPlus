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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader
{

    private static final URLClassLoader classLoader;
    private static Method ADD_URL;

    static
    {
        try
        {
            ADD_URL = URLClassLoader.class.getDeclaredMethod( "addURL", URL.class );
            ADD_URL.setAccessible( true );
        }
        catch ( NoSuchMethodException e )
        {
            ADD_URL = null;
        }

        final ClassLoader loader = DonatorJoinPlus.class.getClassLoader();
        if ( loader instanceof URLClassLoader )
        {
            classLoader = (URLClassLoader) loader;
        }
        else
        {
            throw new IllegalStateException( "Plugin ClassLoader is not instance of URLClassLoader" );
        }
    }

    public static void loadJar( URL url )
    {
        try
        {
            ADD_URL.invoke( classLoader, url );
        }
        catch ( IllegalAccessException | InvocationTargetException e )
        {
            e.printStackTrace();
        }
    }

    public static void loadJar( File file )
    {
        try
        {
            loadJar( file.toURI().toURL() );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }
}