package com.dbsoftwares.djp.slots;

import lombok.Data;

@Data
public class SlotLimit {

    private static int idCounter = 0;

    private final int id;
    private int limit;
    private String permission;

    public SlotLimit(final int limit, final String permission) {
        this.id = idCounter++;
        this.limit = limit;
        this.permission = permission;
    }
}
