package dev.endoy.djp.spigot.commands.subcommands.toggle;

import com.dbsoftwares.commandapi.command.SubCommand;
import com.google.common.collect.ImmutableList;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.user.User;
import dev.endoy.djp.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ToggleMessageSubCommand extends SubCommand
{

    public ToggleMessageSubCommand()
    {
        super( "togglemessages", 0, 0 );
    }

    @Override
    public String getUsage()
    {
        return "/djp togglemessages";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.togglemessages";
    }

    @Override
    public void onExecute( Player player, final String[] args )
    {
        User user = DonatorJoinPlus.i().getUserManager().getOrLoadUserSync( player.getUniqueId() );

        CompletableFuture.runAsync( () -> DonatorJoinPlus.i().getStorage().toggleMessagesMuted( player.getUniqueId(), !user.isMessagesMuted() ) )
                .thenRun( () ->
                {
                    user.setMessagesMuted( !user.isMessagesMuted() );
                    player.sendMessage( Utils.getMessage( user.isMessagesMuted() ? "messages.muted" : "messages.unmuted" ) );
                } );
    }

    @Override
    public void onExecute( final CommandSender sender, final String[] args )
    {
        sender.sendMessage( Utils.getMessage( "not-for-console" ) );
    }

    @Override
    public List<String> getCompletions( final CommandSender sender, final String[] args )
    {
        return ImmutableList.of();
    }

    @Override
    public List<String> getCompletions( Player player, String[] strings )
    {
        return ImmutableList.of();
    }
}
