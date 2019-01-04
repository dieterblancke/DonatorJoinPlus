package com.dbsoftwares.djp;

/*
 * Created by DBSoftwares on 12 mei 2018
 * Developer: Dieter Blancke
 * Project: DonatorJoinPlus
 */

import com.dbsoftwares.commandapi.CommandManager;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.commands.DJCommand;
import com.dbsoftwares.djp.data.RankData;
import com.dbsoftwares.djp.library.Library;
import com.dbsoftwares.djp.library.StandardLibrary;
import com.dbsoftwares.djp.listeners.PlayerListener;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.dbsoftwares.djp.storage.AbstractStorageManager.StorageType;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Getter
public class DonatorJoinPlus extends JavaPlugin {

    @Getter
    private static Logger log;
    private Permission permission;
    private List<RankData> rankData = Lists.newArrayList();
    private boolean disableJoinMessage;
    private boolean disableQuitMessage;
    private boolean usePriorities;
    private boolean usePermissions;
    private AbstractStorageManager storage;
    private IConfiguration configuration;

    public static DonatorJoinPlus i() {
        return getPlugin(DonatorJoinPlus.class);
    }

    @Override
    public void onEnable() {
        final File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            IConfiguration.createDefaultFile(getResource("config.yml"), configFile);
        }

        configuration = IConfiguration.loadYamlConfiguration(configFile);

        // Loading libraries for storage
        for (StandardLibrary standardLibrary : StandardLibrary.values()) {
            final Library library = standardLibrary.getLibrary();

            if (library.isToLoad()) {
                library.load();
            }
        }
        log = LoggerFactory.getLogger("DonatorJoin+");

        loadConfig();

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        CommandManager.getInstance().registerCommand(new DJCommand());

        StorageType type;
        final String typeString = configuration.getString("storage.type").toUpperCase();
        try {

            if (typeString.contains(":")) {
                type = StorageType.valueOf(typeString.split(":")[0]);
            } else {
                type = StorageType.valueOf(typeString);
            }
        } catch (IllegalArgumentException e) {
            type = StorageType.MYSQL;
        }
        try {
            storage = typeString.contains(":")
                    ? type.getManager().getConstructor(String.class).newInstance(typeString.split(":")[1])
                    : type.getManager().getConstructor().newInstance();
            storage.initializeStorage();
        } catch (Exception e) {
            log.error("An error occured: ", e);
        }
    }

    @Override
    public void onDisable() {
        try {
            storage.close();
        } catch (SQLException e) {
            log.error("An error occured", e);
        }
    }

    public void loadConfig() {
        try {
            configuration.reload();
        } catch (IOException e) {
            log.error("An error occured", e);
        }
        rankData.clear();

        final List<ISection> sections = configuration.getSectionList("ranks");

        sections.forEach(section -> {
            final RankData data = new RankData();
            data.fromSection(section);

            rankData.add(data);
        });

        rankData.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));

        disableJoinMessage = configuration.getBoolean("joinmessage");
        disableQuitMessage = configuration.getBoolean("quitmessage");
        usePriorities = configuration.getBoolean("usepriorities");
        usePermissions = configuration.getBoolean("usepermissions");
    }
}