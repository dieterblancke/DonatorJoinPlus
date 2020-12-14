/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.djp.spigot.utils;

import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.djp.utils.Utils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MessageBuilder
{

    @SuppressWarnings( "unchecked" )
    public static TextComponent buildMessage( final Player player, final Object message, final Object... placeholders )
    {
        if (
                ( message instanceof List && ( (List<?>) message ).isEmpty() )
                        || ( message instanceof String && ( (String) message ).isEmpty() )
        )
        {
            return null;
        }
        if ( message instanceof ISection )
        {
            return buildMessage( player, (ISection) message, placeholders );
        }
        else if ( message instanceof List )
        {
            return new TextComponent(SpigotUtils.format( player, (List<String>) message ));
        }
        else
        {
            final String messageString = message.toString();

            if ( messageString.contains( "<nl>" ) )
            {
                return buildMessage( player, Lists.newArrayList( messageString.split( "<nl>" ) ) );
            }

            return new TextComponent( TextComponent.fromLegacyText( SpigotUtils.formatString( player, messageString ) ) );
        }
    }

    public static TextComponent buildMessage( final Player player,
                                              final ISection section,
                                              final Object... placeholders )
    {
        if ( section.isList( "text" ) )
        {
            final TextComponent component = new TextComponent();
            final List<ISection> sections = section.getSectionList( "text" );

            for ( ISection text : sections )
            {
                component.addExtra(
                        buildMessage( player, text, placeholders )
                );
            }
            return component;
        }
        final BaseComponent[] text = searchAndFormat(
                player,
                section.getString( "text" ),
                placeholders
        );
        final TextComponent component = new TextComponent( text );

        if ( section.exists( "hover" ) )
        {
            final BaseComponent[] components = searchHoverMessageAndFormat(
                    player,
                    section,
                    placeholders
            );

            if ( components != null )
            {
                component.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, components ) );
            }
        }
        if ( section.exists( "click" ) )
        {
            component.setClickEvent( new ClickEvent(
                    ClickEvent.Action.valueOf( section.getString( "click.type" ) ),
                    SpigotUtils.formatString( player, Utils.c( format(
                            section.getString( "click.action" ),
                            placeholders
                    ) ) )
            ) );
        }

        return component;
    }

    public static List<TextComponent> buildMessage( final Player player, final List<ISection> sections, final Object... placeholders )
    {
        final List<TextComponent> components = Lists.newArrayList();

        sections.forEach( section -> components.add( buildMessage( player, section, placeholders ) ) );
        return components;
    }

    private static BaseComponent[] searchAndFormat( final Player player,
                                                    final String str,
                                                    final Object... placeholders )
    {
        return TextComponent.fromLegacyText( format( SpigotUtils.formatString( player, str ), placeholders ) );
    }

    private static String formatLine( final String line )
    {
        final String newLine = System.lineSeparator();

        return line.replace( "<nl>", newLine )
                .replace( "%nl%", newLine )
                .replace( "%newline%", newLine )
                .replace( "{nl}", newLine )
                .replace( "{newline}", newLine )
                .replace( "\r\n", newLine )
                .replace( "\n", newLine );
    }

    private static List<String> format( final List<String> list,
                                        final Object... placeholders )
    {
        return list.stream()
                .map( str -> format( str, placeholders ) )
                .collect( Collectors.toList() );
    }

    private static String format( String str,
                                  final Object... placeholders )
    {
        str = formatLine( str );

        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            str = str.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        return str;
    }

    private static BaseComponent[] searchHoverMessageAndFormat( final Player player,
                                                                final ISection section,
                                                                final Object... placeholders )
    {
        if ( section.isList( "hover" ) )
        {
            return SpigotUtils.format(
                    player,
                    format(
                            section.getStringList( "hover" ),
                            placeholders
                    )
            );
        }
        else
        {
            if ( section.getString( "hover" ).isEmpty() )
            {
                return null;
            }
            return searchAndFormat(
                    player,
                    section.getString( "hover" ),
                    placeholders
            );
        }
    }
}