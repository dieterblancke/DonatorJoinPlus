package com.dbsoftwares.djp.utils;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.djp.DonatorJoinPlus;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Utils {

    public static final String TOGGLE_KEY = "DJP_TOGGLE";

    private Utils() {
    }

    public static void setMetaData(final Player player, final String key, final Object value) {
        // Removing first to be sure
        player.removeMetadata(key, DonatorJoinPlus.i());

        // Setting meta data
        player.setMetadata(key, new FixedMetadataValue(DonatorJoinPlus.i(), value));
    }

    public static Object getMetaData(final Player player, final String key) {
        return getMetaData(player, key, null);
    }

    public static Object getMetaData(final Player player, final String key, Object defaultValue) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.getOwningPlugin().equals(DonatorJoinPlus.i())) {
                return meta.value();
            }
        }
        return defaultValue;
    }

    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }

    public static void spawnFirework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta fireworkmeta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .withTrail().withFlicker()
                .withFade(Color.GREEN)
                .withColor(Color.WHITE).withColor(Color.YELLOW)
                .withColor(Color.BLUE).withColor(Color.FUCHSIA)
                .withColor(Color.PURPLE).withColor(Color.MAROON)
                .withColor(Color.LIME).withColor(Color.ORANGE)
                .with(FireworkEffect.Type.BALL_LARGE);

        fireworkmeta.addEffect(builder.build());
        fireworkmeta.setPower(1);
        firework.setFireworkMeta(fireworkmeta);
    }

    public static boolean contains(String[] groups, String group) {
        for (String g : groups) {
            if (g.equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

    public static String c(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}