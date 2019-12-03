/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.djp.library;

import com.dbsoftwares.djp.DonatorJoinCore;
import lombok.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class Library
{

    private final static List<Library> registry = new ArrayList<>();

    private final String name;
    private final String className;
    private final String downloadURL;
    private final String version;
    private final boolean toLoad;

    public Library( String name, String className, String downloadURL, String version, boolean toLoad )
    {
        this.name = name;
        this.className = className;
        this.downloadURL = downloadURL.replace( "{version}", version );
        this.version = version;
        this.toLoad = toLoad;

        registry.add( this );
    }

    public static Library getLibrary( final String name )
    {
        return registry.stream().filter( library -> library.getName().equalsIgnoreCase( name ) ).findFirst().orElse( null );
    }

    public boolean isPresent()
    {
        try
        {
            Class.forName( className );
            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    public void load()
    {
        if ( isPresent() )
        {
            return;
        }
        final File folder = new File( DonatorJoinCore.getInstance().getDataFolder(), "libraries" );
        if ( !folder.exists() && !folder.mkdir() )
        {
            return;
        }
        final File path = new File( folder, String.format( "%s-v%s.jar", name.toLowerCase(), version ) );

        // Download libary if not present
        if ( !path.exists() )
        {
            System.out.println( "Downloading libary for " + toString() );

            try ( final InputStream input = new URL( downloadURL ).openStream();
                  final ReadableByteChannel channel = Channels.newChannel( input );
                  final FileOutputStream output = new FileOutputStream( path ) )
            {

                output.getChannel().transferFrom( channel, 0, Long.MAX_VALUE );
                System.out.println( "Successfully downloaded libary for " + toString() );

                System.out.println( "Removing older versions of " + toString() );
                getOutdatedFiles( folder ).forEach( File::delete );
                System.out.println( "Successfully removed older versions of " + toString() );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( "Failed downloading library for " + toString().toLowerCase(), e );
            }
        }

        JarClassLoader.loadJar( path );
        System.out.println( "Loaded " + name + " libary!" );
    }

    private Collection<File> getOutdatedFiles( final File folder )
    {
        final List<File> outdatedFiles = new ArrayList<>();
        final String name = toString().toLowerCase();

        for ( File library : folder.listFiles() )
        {
            final String jarName = library.getName();

            if ( jarName.startsWith( name ) && !jarName.equals( String.format( "%s-v%s.jar", name.toLowerCase(), version ) ) )
            {
                outdatedFiles.add( library );
            }
        }

        return outdatedFiles;
    }

    @Override
    public String toString()
    {
        return name;
    }
}