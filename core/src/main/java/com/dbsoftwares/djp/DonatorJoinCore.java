package com.dbsoftwares.djp;

public class DonatorJoinCore
{

    private static DonatorJoinBase instance = null;

    public static DonatorJoinBase getInstance()
    {
        return instance;
    }

    public static void setInstance( final DonatorJoinBase inst )
    {
        instance = inst;
    }
}
