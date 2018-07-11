package com.dbsoftwares.djp;

/*
 * Created by DBSoftwares on 12 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.djp.commands.DJCommand;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.listeners.PlayerListener;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Map;

public class DonatorJoinPlus extends JavaPlugin {

    @Getter private Permission permission;
    @Getter private List<RankData> rankData = Lists.newArrayList();

    @Getter private boolean disableJoinMessage;
    @Getter private boolean disableQuitMessage;
    @Getter private boolean usePriorities;
    @Getter private boolean usePermissions;

    @Override
    public void onEnable() {
        loadConfig();

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("donatorjoin").setExecutor(new DJCommand());
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