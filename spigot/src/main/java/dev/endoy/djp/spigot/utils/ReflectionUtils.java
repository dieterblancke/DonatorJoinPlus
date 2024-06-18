package dev.endoy.djp.spigot.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {

    private static final Map<Class<?>, Class<?>> TYPES = new HashMap<>();

    public static Object getHandle(Class<?> clazz, Object o) {
        try {
            return clazz.getMethod("getHandle").invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes) {
        Class<?>[] t = toPrimitiveTypeArray(paramTypes);
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = toPrimitiveTypeArray(m.getParameterTypes());
            if (m.getName().equals(name) && equalsTypeArray(types, t)) {
                return m;
            }
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSpigotVersion() {
        String version;

        try {
            version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        } catch (Exception e) {
            try {
                Method getServer = getMethod("getServer", Bukkit.getServer().getClass());
                Object server = getServer.invoke(Bukkit.getServer());
                Method getVersion = getMethod("getServerVersion", server.getClass());

                version = (String) getVersion.invoke(server);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
                version = null;
            }
        }

        return version;
    }

    private static Class<?> getPrimitiveType(Class<?> clazz) {
        return TYPES.getOrDefault(clazz, clazz);
    }

    private static Class<?>[] toPrimitiveTypeArray(Class<?>[] classes) {
        int a = classes != null ? classes.length : 0;
        Class<?>[] types = new Class<?>[a];
        for (int i = 0; i < a; i++) {
            types[i] = getPrimitiveType(classes[i]);
        }
        return types;
    }

    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
        if (a.length != o.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!a[i].equals(o[i]) && !a[i].isAssignableFrom(o[i])) {
                return false;
            }
        }
        return true;
    }


    public static int getNumericVersion() {
        return Integer.parseInt(getSpigotVersion().replaceAll("[^0-9]", ""));
    }
}