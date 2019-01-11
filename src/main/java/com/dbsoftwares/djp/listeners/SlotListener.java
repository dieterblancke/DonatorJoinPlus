package com.dbsoftwares.djp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class SlotListener implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        System.out.println("AsyncPlayerPreLoginEvent: " + event.getName());
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        System.out.println("PlayerLoginEvent: " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("PlayerJoinEvent: " + event.getPlayer().getName());
    }
}
