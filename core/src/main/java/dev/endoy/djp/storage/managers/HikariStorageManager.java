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

import be.dieterblancke.configuration.api.ISection;
import dev.endoy.djp.DonatorJoinCore;
import dev.endoy.djp.storage.AbstractStorageManager;
import dev.endoy.djp.user.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public abstract class HikariStorageManager extends AbstractStorageManager
{

    protected HikariConfig config;
    protected HikariDataSource dataSource;

    public HikariStorageManager( final StorageType type, final ISection section )
    {
        this( type, section, new HikariConfig() );
    }

    public HikariStorageManager( final StorageType type, final ISection section, final HikariConfig config )
    {
        super( type );

        this.config = config;
        if ( getDataSourceClass() != null )
        {
            config.setDataSourceClassName( getDataSourceClass() );
        }

        config.setMaximumPoolSize( section.getInteger( "pool.max-pool-size" ) );
        config.setMinimumIdle( section.getInteger( "pool.min-idle" ) );
        config.setMaxLifetime( section.getLong( "pool.max-lifetime" ) * 1000 );
        config.setConnectionTimeout( section.getLong( "pool.connection-timeout" ) * 1000 );

        config.setPoolName( "DonatorJoinPlus" );
        config.setLeakDetectionThreshold( 10000 );
        config.setConnectionTestQuery( "/* DonatorJoinPlus ping */ SELECT 1;" );
        config.setInitializationFailTimeout( -1 );

        final ISection propertySection = section.getSection( "properties" );
        for ( String key : propertySection.getKeys() )
        {
            if ( config.getDataSourceClassName() == null )
            {
                continue;
            }
            try
            {
                final Class<?> clazz = Class.forName( config.getDataSourceClassName() );

                if ( this.hasProperty( clazz, key ) )
                {
                    config.addDataSourceProperty( key, propertySection.get( key ) );
                }
            }
            catch ( ClassNotFoundException e )
            {
                // continue
            }
        }
    }

    protected void setupDataSource()
    {
        if ( dataSource == null )
        {
            dataSource = new HikariDataSource( config );
        }
    }

    @Override
    public void initializeStorage() throws Exception
    {
        super.initializeStorage();

        try ( Connection connection = getConnection() )
        {
            final DatabaseMetaData metaData = connection.getMetaData();

            initSlotGroupColumn( connection, metaData );
            initJoinSoundColumn( connection, metaData );
            initLeaveSoundColumn( connection, metaData );
            initSoundToggledColumn( connection, metaData );
            initFireworkToggledColumn( connection, metaData );
            initMessagesMutedColumn( connection, metaData );
            initVolumePitchColumns(connection, metaData);
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }

    private void initSlotGroupColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        if ( checkIfColumnDoesNotExist( connection, metaData, "slotgroup" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD slotgroup VARCHAR(1) NOT NULL DEFAULT 'none';" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private void initJoinSoundColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        if ( checkIfColumnDoesNotExist( connection, metaData, "joinsound" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD joinsound VARCHAR(1) DEFAULT NULL;" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private void initLeaveSoundColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        if ( checkIfColumnDoesNotExist( connection, metaData, "leavesound" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD leavesound VARCHAR(1) DEFAULT NULL;" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private void initSoundToggledColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        if ( checkIfColumnDoesNotExist( connection, metaData, "soundtoggled" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD soundtoggled TINYINT(1) NOT NULL DEFAULT 0;" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private void initFireworkToggledColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        if ( checkIfColumnDoesNotExist( connection, metaData, "fireworktoggled" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD fireworktoggled TINYINT(1) NOT NULL DEFAULT 0;" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private void initMessagesMutedColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        if ( checkIfColumnDoesNotExist( connection, metaData, "messagesmuted" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD messagesmuted TINYINT(1) NOT NULL DEFAULT 0;" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private void initVolumePitchColumns( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        String intType = switch ( this.getType() ) {
            case MYSQL -> "INT(11)";
            case SQLITE -> "INTEGER";
            case H2 -> "INT";
            case FILE -> "";
        };

        if ( checkIfColumnDoesNotExist( connection, metaData, "join_volume" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD join_volume " + intType + " DEFAULT 20;" )
            )
            {
                pstmt.execute();
            }
        }
        if ( checkIfColumnDoesNotExist( connection, metaData, "join_pitch" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD join_pitch " + intType + " DEFAULT -20;" )
            )
            {
                pstmt.execute();
            }
        }
        if ( checkIfColumnDoesNotExist( connection, metaData, "leave_volume" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD leave_volume " + intType + " DEFAULT 20;" )
            )
            {
                pstmt.execute();
            }
        }
        if ( checkIfColumnDoesNotExist( connection, metaData, "leave_pitch" ) )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement(
                    "ALTER TABLE djp_data ADD leave_pitch " + intType + " DEFAULT -20;" )
            )
            {
                pstmt.execute();
            }
        }
    }

    private boolean checkIfColumnDoesNotExist(final Connection connection,
                                               final DatabaseMetaData metaData,
                                               final String column ) throws SQLException
    {
        try ( ResultSet rs = metaData.getColumns( null, null, "djp_data", null ) )
        {
            while ( rs.next() )
            {
                final String columnName = rs.getString( "COLUMN_NAME" );

                if ( columnName.equalsIgnoreCase( column ) )
                {
                    return false;
                }
            }
        }
        return true;
    }

    protected abstract String getDataSourceClass();

    @Override
    public void close()
    {
        dataSource.close();
    }

    @Override
    public boolean exists( final UUID uuid )
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
        return toggled;
    }

    @Override
    public void toggle( final UUID uuid, final boolean toggled )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET toggled = ? WHERE uuid = ?;" ) )
            {
                pstmt.setBoolean( 1, toggled );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public void setJoinSound( final UUID uuid, final String sound, final float volume, final float pitch )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET joinsound = ?, join_volume = ?, join_pitch = ? WHERE uuid = ?;" ) )
            {
                pstmt.setString( 1, sound );
                pstmt.setInt( 2, (int) volume );
                pstmt.setInt( 3, (int) pitch );
                pstmt.setString( 4, uuid.toString() );
                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public void setLeaveSound( final UUID uuid, final String sound, final float volume, final float pitch )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET leavesound = ?, leave_volume = ?, leave_pitch = ? WHERE uuid = ?;" ) )
            {
                pstmt.setString( 1, sound );
                pstmt.setInt( 2, (int) volume );
                pstmt.setInt( 3, (int) pitch );
                pstmt.setString( 4, uuid.toString() );
                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public void toggleSound( UUID uuid, boolean toggled )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET soundtoggled = ? WHERE uuid = ?;" ) )
            {
                pstmt.setBoolean( 1, toggled );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public void toggleFirework( UUID uuid, boolean toggled )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET fireworktoggled = ? WHERE uuid = ?;" ) )
            {
                pstmt.setBoolean( 1, toggled );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public void toggleMessagesMuted( UUID uuid, boolean toggled )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "UPDATE djp_data SET messagesmuted = ? WHERE uuid = ?;" ) )
            {
                pstmt.setBoolean( 1, toggled );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public boolean isSoundToggled( final UUID uuid )
    {
        boolean toggled = false;
        try ( Connection connection = getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT soundtoggled FROM djp_data WHERE uuid = ? AND soundtoggled = ?;" ) )
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
        return toggled;
    }

    @Override
    public boolean isFireworkToggled( final UUID uuid )
    {
        boolean toggled = false;
        try ( Connection connection = getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT fireworktoggled FROM djp_data WHERE uuid = ? AND fireworktoggled = ?;" ) )
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
        return toggled;
    }

    @Override
    public boolean isMessagesMuted( final UUID uuid )
    {
        boolean toggled = false;
        try ( Connection connection = getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT messagesmuted FROM djp_data WHERE uuid = ? AND messagesmuted = ?;" ) )
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
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
        return toggled;
    }

    @Override
    public User getUser( final UUID uuid )
    {
        if ( !exists( uuid ) )
        {
            createUser( uuid );
            return new User( uuid );
        }
        User user = null;

        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "SELECT * FROM djp_data WHERE uuid = ?;" ) )
            {
                pstmt.setString( 1, uuid.toString() );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        user = new User(
                                uuid,
                                rs.getBoolean( "toggled" ),
                                rs.getString( "slotgroup" ),
                                rs.getString( "joinsound" ),
                                rs.getInt( "join_volume" ),
                                rs.getInt( "join_pitch" ),
                                rs.getString( "leavesound" ),
                                rs.getInt( "leave_volume" ),
                                rs.getInt( "leave_pitch" ),
                                rs.getBoolean( "soundtoggled" ),
                                rs.getBoolean( "fireworktoggled" ),
                                rs.getBoolean( "messagesmuted" )
                        );
                    }
                }
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
        return user == null ? new User( uuid ) : user;
    }

    private void createUser( final UUID uuid )
    {
        try ( Connection connection = getConnection() )
        {
            try ( PreparedStatement pstmt = connection.prepareStatement( "INSERT INTO djp_data(uuid) VALUES (?);" ) )
            {
                pstmt.setString( 1, uuid.toString() );

                pstmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            DonatorJoinCore.getInstance().getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    private boolean hasProperty( final Class<?> clazz, final String key )
    {
        final String methodName = "set" + key.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + key.substring( 1 );

        return Arrays.stream( clazz.getDeclaredMethods() )
                .anyMatch( method -> method.getName().equalsIgnoreCase( methodName ) );
    }
}
