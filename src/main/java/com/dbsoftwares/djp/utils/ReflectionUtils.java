package com.dbsoftwares.djp.utils;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ReflectionUtils
{

    private static final Map<Class<?>, Class<?>> TYPES = Maps.newHashMap();

    public static Object getHandle( Class<?> clazz, Object o )
    {
        try
        {
            return clazz.getMethod( "getHandle" ).invoke( o );
        } catch ( Exception e )
        {
            return null;
        }
    }

    public static Object getHandle( Object o )
    {
        try
        {
            return getMethod( "getHandle", o.getClass() ).invoke( o );
        } catch ( Exception e )
        {
            return null;
        }
    }

    public static Object getConnection( Object handle )
    {
        try
        {
            return getField( handle.getClass(), "playerConnection" ).get( handle );
        } catch ( Exception e )
        {
            return null;
        }
    }

    public static Class<?> getClass( String name )
    {
        try
        {
            return Class.forName( name );
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMS( String name )
    {
        String cname = "net.minecraft.server." + getVersionName() + "." + name;
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName( cname );
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getCraftBukkitClass( String path )
    {
        String cname = "org.bukkit.craftbukkit." + getVersionName() + "." + path;
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName( cname );
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Method getMethod( String name, Class<?> clazz, Class<?>... paramTypes )
    {
        Class<?>[] t = toPrimitiveTypeArray( paramTypes );
        for ( Method m : clazz.getMethods() )
        {
            Class<?>[] types = toPrimitiveTypeArray( m.getParameterTypes() );
            if ( m.getName().equals( name ) && equalsTypeArray( types, t ) )
            {
                return m;
            }
        }
        return null;
    }

    public static Method getMethod( Class<?> clazz, String name, Class<?>... args )
    {
        for ( Method m : clazz.getMethods() )
        {
            if ( m.getName().equals( name ) && ( args.length == 0 || classList( args, m.getParameterTypes() ) ) )
            {
                m.setAccessible( true );
                return m;
            }
        }
        return null;
    }

    public static Method getMethod( String className, PackageType packageType, String methodName, Class<?>... parameterTypes )
    {
        return getMethod( packageType.getClass( className ), methodName, parameterTypes );
    }

    public static Field getField( Class<?> clazz, String name )
    {
        try
        {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );
            return field;
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField( String className, PackageType packageType, String fieldName ) throws SecurityException
    {
        return getField( packageType.getClass( className ), fieldName );
    }

    public static Object getValue( Object instance, Class<?> clazz, String fieldName ) throws IllegalArgumentException, IllegalAccessException, SecurityException
    {
        return getField( clazz, fieldName ).get( instance );
    }

    public static Object getValue( Object instance, String className, PackageType packageType, String fieldName ) throws IllegalArgumentException, IllegalAccessException, SecurityException
    {
        return getValue( instance, packageType.getClass( className ), fieldName );
    }

    public static Object getValue( Object instance, String fieldName ) throws IllegalArgumentException, IllegalAccessException, SecurityException
    {
        return getValue( instance, instance.getClass(), fieldName );
    }

    public static void setValue( Object instance, Class<?> clazz, String fieldName, Object value ) throws IllegalArgumentException, IllegalAccessException, SecurityException
    {
        getField( clazz, fieldName ).set( instance, value );
    }

    public static void setValue( Object instance, String className, PackageType packageType, String fieldName, Object value ) throws IllegalArgumentException, IllegalAccessException, SecurityException
    {
        setValue( instance, packageType.getClass( className ), fieldName, value );
    }

    public static void setValue( Object instance, String fieldName, Object value ) throws IllegalArgumentException, IllegalAccessException, SecurityException
    {
        setValue( instance, instance.getClass(), fieldName, value );
    }

    public static Constructor<?> getConstructor( Class<?> clazz, Class<?>... parameterTypes )
    {
        try
        {
            Constructor<?> constructor = clazz.getConstructor( parameterTypes );
            constructor.setAccessible( true );
            return constructor;
        } catch ( Exception e )
        {
            return null;
        }
    }

    public static Constructor<?> getConstructor( String className, PackageType packageType, Class<?>... parameterTypes )
    {
        return getConstructor( packageType.getClass( className ), parameterTypes );
    }

    public static String getVersionName()
    {
        return Bukkit.getServer().getClass().getPackage().getName().substring( 23 );
    }

    private static Class<?> getPrimitiveType( Class<?> clazz )
    {
        return TYPES.getOrDefault( clazz, clazz );
    }

    private static Class<?>[] toPrimitiveTypeArray( Class<?>[] classes )
    {
        int a = classes != null ? classes.length : 0;
        Class<?>[] types = new Class<?>[a];
        for ( int i = 0; i < a; i++ )
        {
            types[i] =getPrimitiveType( classes[i] );
        }
        return types;
    }

    private static boolean equalsTypeArray( Class<?>[] a, Class<?>[] o )
    {
        if ( a.length != o.length )
        {
            return false;
        }
        for ( int i = 0; i < a.length; i++ )
        {
            if ( !a[i].equals( o[i] ) && !a[i].isAssignableFrom( o[i] ) )
            {
                return false;
            }
        }
        return true;
    }

    private static boolean classList( Class<?>[] l1, Class<?>[] l2 )
    {
        boolean equal = true;
        if ( l1.length != l2.length )
        {
            return false;
        }
        for ( int i = 0; i < l1.length; i++ )
        {
            if ( l1[i] != l2[i] )
            {
                equal = false;
                break;
            }
        }
        return equal;
    }

    public static String getServerVersion()
    {
        return Bukkit.getServer().getClass().getPackage().getName().substring( 23 );
    }

    public static void sendPacket( Player player, Object packet )
    {
        try
        {
            Object handle = getHandle( player );
            Object connection = getField( handle.getClass(), "playerConnection" ).get( handle );
            Method sendPacket = getMethod( connection.getClass(), "sendPacket" );

            sendPacket.invoke( connection, packet );
        } catch ( IllegalAccessException | InvocationTargetException e )
        {
            e.printStackTrace();
        }
    }

    public enum PackageType
    {
        MINECRAFT_SERVER( "net.minecraft.server." + getServerVersion() ),
        CRAFTBUKKIT( "org.bukkit.craftbukkit." + getServerVersion() ),
        CRAFTBUKKIT_BLOCK( CRAFTBUKKIT, "block" ),
        CRAFTBUKKIT_CHUNKIO( CRAFTBUKKIT, "chunkio" ),
        CRAFTBUKKIT_COMMAND( CRAFTBUKKIT, "command" ),
        CRAFTBUKKIT_CONVERSATIONS( CRAFTBUKKIT, "conversations" ),
        CRAFTBUKKIT_ENCHANTMENS( CRAFTBUKKIT, "enchantments" ),
        CRAFTBUKKIT_ENTITY( CRAFTBUKKIT, "entity" ),
        CRAFTBUKKIT_EVENT( CRAFTBUKKIT, "event" ),
        CRAFTBUKKIT_GENERATOR( CRAFTBUKKIT, "generator" ),
        CRAFTBUKKIT_HELP( CRAFTBUKKIT, "help" ),
        CRAFTBUKKIT_INVENTORY( CRAFTBUKKIT, "inventory" ),
        CRAFTBUKKIT_MAP( CRAFTBUKKIT, "map" ),
        CRAFTBUKKIT_METADATA( CRAFTBUKKIT, "metadata" ),
        CRAFTBUKKIT_POTION( CRAFTBUKKIT, "potion" ),
        CRAFTBUKKIT_PROJECTILES( CRAFTBUKKIT, "projectiles" ),
        CRAFTBUKKIT_SCHEDULER( CRAFTBUKKIT, "scheduler" ),
        CRAFTBUKKIT_SCOREBOARD( CRAFTBUKKIT, "scoreboard" ),
        CRAFTBUKKIT_UPDATER( CRAFTBUKKIT, "updater" ),
        CRAFTBUKKIT_UTIL( CRAFTBUKKIT, "util" );

        private final String path;

        PackageType( String path )
        {
            this.path = path;
        }

        PackageType( PackageType parent, String path )
        {
            this( parent + "." + path );
        }

        public String getPath()
        {
            return path;
        }

        public Class<?> getClass( String className )
        {
            try
            {
                return Class.forName( this + "." + className );
            } catch ( ClassNotFoundException e )
            {
                return null;
            }
        }

        @Override
        public String toString()
        {
            return path;
        }
    }
}