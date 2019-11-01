package com.dbsoftwares.djp.commands.subcommands.toggle;

public class ToggleSubCommand extends ToggableSubCommand
{

    public ToggleSubCommand()
    {
        super( "toggle", 0, 1 );
    }

    @Override
    public String getUsage()
    {
        return "/djp toggle [player]";
    }
}
