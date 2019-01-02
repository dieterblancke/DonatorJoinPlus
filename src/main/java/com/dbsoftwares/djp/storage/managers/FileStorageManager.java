package com.dbsoftwares.djp.storage.managers;

import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.djp.DonatorJoinPlus;
import com.dbsoftwares.djp.storage.AbstractStorageManager;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class FileStorageManager extends AbstractStorageManager {

    private final IConfiguration storage;

    public FileStorageManager(final String type) {
        super(StorageType.FILE);

        // load configuration file
        FileStorageType storageType;
        try {
            storageType = FileStorageType.valueOf(type);
        } catch (IllegalArgumentException e) {
            storageType = FileStorageType.JSON;
        }
        final File storageFile = new File(
                DonatorJoinPlus.i().getDataFolder(),
                "file-storage." + (storageType.equals(FileStorageType.JSON) ? "json" : "yml")
        );
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                DonatorJoinPlus.getLogger().error("An error occured: ", e);
            }
        }
        this.storage = IConfiguration.loadConfiguration(storageType, storageFile);
        if (!storage.exists("toggled")) {
            storage.set("toggled", Lists.newArrayList());
            save();
        }
    }

    @Override
    public boolean isToggled(UUID uuid) {
        final List<String> toggleList = storage.getStringList("toggled");

        return toggleList.contains(uuid.toString());
    }

    @Override
    public void toggle(UUID uuid, boolean toggled) {
        final List<String> toggleList = storage.getStringList("toggled");
        final String uuidString = uuid.toString();

        if (toggled) {
            if (!toggleList.contains(uuidString)) {
                toggleList.add(uuidString);
            }
        } else {
            toggleList.remove(uuidString);
        }

        storage.set("toggled", toggleList);
        if (DonatorJoinPlus.i().getConfig().getBoolean("storage.save-per-toggle")) {
            save();
        }
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void close() {
        save();
    }

    private void save() {
        try {
            storage.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
