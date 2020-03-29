package com.hadroncfy.multichat;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.kyori.text.TextComponent;

public class ServerListCommand implements Command {
    private final MultiChat h;

    public static final String ALIAS = "sl";

    public ServerListCommand(MultiChat c){
        h = c;
    }
    
    @Override
    public void execute(CommandSource source, String @NonNull [] args) {
        if (source instanceof Player){
            source.sendMessage(h.buildServerList(((Player)source).getUsername()));
        }
        else {
            source.sendMessage(TextComponent.of("Error: caller is not a player"));
        }
    }

}