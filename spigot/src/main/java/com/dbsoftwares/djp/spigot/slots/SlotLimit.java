package com.dbsoftwares.djp.spigot.slots;

import be.dieterblancke.configuration.api.ISection;
import lombok.Data;

@Data
public class SlotLimit
{

    private static int idCounter = 0;

    private final int id;
    private final String name;
    private int limit;
    private String permission;

    public SlotLimit( final ISection section )
    {
        this( section.getString( "name" ), section.getInteger( "additional" ), section.getString( "permission" ) );
    }

    public SlotLimit( final String name, final int limit, final String permission )
    {
        this.id = idCounter++;
        this.name = name;
        this.limit = limit;
        this.permission = permission;
    }

    public static void resetCounter()
    {
        idCounter = 0;
    }
}
