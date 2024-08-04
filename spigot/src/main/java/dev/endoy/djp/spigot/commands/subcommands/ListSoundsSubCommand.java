package dev.endoy.djp.spigot.commands.subcommands;

import dev.endoy.spigot.commandapi.command.SubCommand;
import dev.endoy.djp.spigot.DonatorJoinPlus;
import dev.endoy.djp.spigot.utils.ReflectionUtils;
import dev.endoy.djp.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class ListSoundsSubCommand extends SubCommand
{

    public ListSoundsSubCommand()
    {
        super( "listsounds" );
    }

    @Override
    public String getUsage()
    {
        return "/djp listsounds";
    }

    @Override
    public String getPermission()
    {
        return "donatorjoinplus.listsounds";
    }

    @Override
    public void onExecute( final Player player, final String[] args )
    {
        this.onExecute( (CommandSender) player, args );
    }

    @Override
    public void onExecute( final CommandSender sender, final String[] args )
    {
        Bukkit.getScheduler().runTaskAsynchronously( DonatorJoinPlus.i(), () ->
        {
            final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            try
            {
                final HttpURLConnection con = (HttpURLConnection) new URL( "https://paste.dbsoftwares.eu/documents/" ).openConnection();

                con.addRequestProperty(
                        "User-Agent", "DonatorJoin+ v" + DonatorJoinPlus.i().getDescription().getVersion()
                );
                con.setRequestMethod( "POST" );
                con.setRequestProperty( "Content-Type", "application/json" );
                con.setRequestProperty( "charset", "utf-8" );
                con.setDoOutput( true );

                final StringBuilder builder = new StringBuilder();

                builder.append( "Sounds for Spigot " ).append( ReflectionUtils.getSpigotVersion() ).append( ": \n" );

                for ( Sound sound : Sound.values() )
                {
                    builder.append( "- " ).append( sound.toString() ).append( "\n" );
                }

                final OutputStream out = con.getOutputStream();
                out.write( builder.toString().getBytes( StandardCharsets.UTF_8 ) );
                out.close();

                if ( con.getResponseCode() == 429 )
                {
                    sender.sendMessage( Utils.prefixedMessage( "&eYou have exceeded the allowed amount of dumps per minute." ) );
                    return;
                }

                final String response = CharStreams.toString( new InputStreamReader( con.getInputStream() ) );
                con.getInputStream().close();

                final JsonObject jsonResponse = gson.fromJson( response, JsonObject.class );

                if ( !jsonResponse.has( "key" ) )
                {
                    throw new IllegalStateException( "Could not create dump correctly, did something go wrong?" );
                }

                sender.sendMessage( Utils.prefixedMessage(
                        "&eSuccessfully created a dump at: &bhttps://paste.dbsoftwares.eu/"
                                + jsonResponse.get( "key" ).getAsString() + ".dump"
                ) );
            }
            catch ( IOException e )
            {
                sender.sendMessage(
                        Utils.prefixedMessage( "Could not create dump. Please check the console for errors." )
                );
                DonatorJoinPlus.i().getLogger().warning( "Could not create dump request" );
                DonatorJoinPlus.i().getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        } );
    }

    @Override
    public List<String> getCompletions( final CommandSender sender, final String[] args )
    {
        return ImmutableList.of();
    }

    @Override
    public List<String> getCompletions( final Player player, final String[] args )
    {
        return ImmutableList.of();
    }
}
