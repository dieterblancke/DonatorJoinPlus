package com.dbsoftwares.djp.utils;

/*
 * Created by DBSoftwares on 13 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.MetadataValue;

public class Utils {

    private Utils() {
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