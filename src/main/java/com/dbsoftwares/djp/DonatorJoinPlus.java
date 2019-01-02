package com.dbsoftwares.djp;

/*
 * Created by DBSoftwares on 12 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.djp.commands.DJCommand;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.library.Library;
import com.dbsoftwares.djp.library.StandardLibrary;
import com.dbsoftwares.djp.listeners.PlayerListener;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DonatorJoinPlus extends JavaPlugin {

    @Getter
    private Permission permission;
    @Getter
    private List<RankData> rankData = Lists.newArrayList();

    @Getter
    private boolean disableJoinMessage;
    @Getter
    private boolean disableQuitMessage;
    @Getter
    private boolean usePriorities;
    @Getter
    private boolean usePermissions;
    @Getter
    private AbstractStorageManager storage;

    @Getter
    private static Logger logger = LoggerFactory.getLogger("BungeeUtilisals");

    public static DonatorJoinPlus i() {
        return getPlugin(DonatorJoinPlus.class);
    }

    @Override
    public void onEnable() {
        loadConfig();

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("donatorjoin").setExecutor(new DJCommand());

        // Loading libraries for storage
        for (StandardLibrary standardLibrary : StandardLibrary.values()) {
            final Library library = standardLibrary.getLibrary();

            if (library.isToLoad()) {
                library.load();
            }
        }

        // TODO: load storage
    }

    public void loadConfig() {
        saveDefaultConfig();
        rankData.clear();

        List list = getConfig().getList("ranks");

        for (Object rank : list) {
            RankData data = new RankData();
            data.fromMap((Map) rank);

            rankData.add(data);
        }

        rankData.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));

        disableJoinMessage = getConfig().getBoolean("joinmessage");
        disableQuitMessage = getConfig().getBoolean("quitmessage");
        usePriorities = getConfig().getBoolean("usepriorities");
        usePermissions = getConfig().getBoolean("usepermissions");
    }
}