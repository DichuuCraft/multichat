package com.hadroncfy.multichat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;


public class ServerListCommand implements RawCommand {
    private final MultiChat h;

    public static final String ALIAS = "sl";

    public ServerListCommand(MultiChat c){
        this.h = c;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source =  invocation.source();
        if (source instanceof Player){
            source.sendMessage(h.buildServerList(((Player)source).getUsername()));
        } else {
            source.sendMessage(Component.text("Error: caller is not a player"));
        }
    }
}