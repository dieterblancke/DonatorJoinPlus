package com.dbsoftwares.djp.bungee.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerStorageData
{
    private final UUID uuid;
    private boolean exists;
    private boolean toggled;
    private boolean networkJoin;
}