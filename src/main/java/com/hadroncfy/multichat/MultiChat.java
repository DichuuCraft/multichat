package com.hadroncfy.multichat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.hadroncfy.multichat.componentrenderer.Context;
import com.hadroncfy.multichat.componentrenderer.FormatVar;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;

@Plugin(id = "multichat", name = "MultiChat", description = "Simple multi-server chat plugin for Velocity", version = "1.0", authors = {
        "hadroncfy" })
public class MultiChat {
    @Inject
    private ProxyServer server;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path dataPath;

    private Config config;

    private Context rootCtx = new Context();

    private Gson gson = GsonComponentSerializer.populate(new GsonBuilder()).setPrettyPrinting().create();

    private File getConfigFile(){
        return new File(dataPath.toFile(), "config.json");
    }

    private void loadConfig() {
        File cfgDir = dataPath.toFile();
        if (!cfgDir.exists()){
            cfgDir.mkdirs();
        }
        try {
            File configFile = getConfigFile();
            if (configFile.exists()){
                try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)){
                    config = gson.fromJson(reader, Config.class);
                }
            }
            else {
                config = new Config();
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)){
                writer.write(gson.toJson(config));
            }
            logger.info("created config file");
        }
        catch(Throwable e){
            logger.error("Cannot load config file", e);
        }
    }

    @Subscribe
    public void onProxyInitialized(ProxyInitializeEvent e){
        loadConfig();
        server.getCommandManager().register(new ServerListCommand(this), ServerListCommand.ALIAS);
    }

    @Subscribe
    public void onProxyReloaded(ProxyReloadEvent e){
        loadConfig();
    }

    @Subscribe
    public void onPreConnect(ServerPreConnectEvent e){
        if (e.getResult().isAllowed()){
            Player p = e.getPlayer();
            if (p.getCurrentServer().isPresent()){
                server.broadcast(buildServerJoinMessage(p, p.getCurrentServer().get().getServer(), e.getResult().getServer().get()));
            }
            else {
                server.broadcast(buildServerJoinMessage(p, null, e.getResult().getServer().get()));
            }
        }
    }

    @Subscribe
    public void onDisconnected(DisconnectEvent e){
        Player p = e.getPlayer();
        Context ctx = rootCtx.newContext().player(p);
        p.getCurrentServer().ifPresent(s -> {
            String name = s.getServerInfo().getName();
            Component alias = config.serverAlias.get(name);
            ctx.local(FormatVar.CURRENT_SERVER_NAME, name).local(FormatVar.CURRENT_SERVER_ALIAS, alias);
        });
        server.broadcast(ctx.format(config.formats.proxyLeft));
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent e){
        if (config.sendHelloMessage){
            e.getPlayer().sendMessage(buildServerList(e.getPlayer().getUsername()));
        }
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent e){
        e.getPlayer().getCurrentServer().ifPresent(s1 -> {
            RegisteredServer from = s1.getServer();
            Component msg = buildChatMessage(e.getPlayer(), from, e.getMessage());
            for (Player p: server.getAllPlayers()){
                p.getCurrentServer().ifPresent(s2 -> {
                    if (s2.getServer() != from){
                        p.sendMessage(msg);
                    }
                });
            }
        });
    }

    private Component buildChatMessage(Player src, RegisteredServer from, String msg){
        String serverName = from.getServerInfo().getName();
        Component alias = config.serverAlias.get(serverName);
        Context ctx = rootCtx.newContext()
            .local(FormatVar.CURRENT_SERVER_NAME, from.getServerInfo().getName())
            .local(FormatVar.CURRENT_SERVER_ALIAS, alias)
            .local(FormatVar.PLAYER_NAME, src.getUsername())
            .local(FormatVar.PLAYER_UUID, src.getUsername().toString())
            .local(FormatVar.PLAYER_PING, Long.toString(src.getPing()))
            .local(FormatVar.MESSAGE, msg);
        return ctx.format(config.formats.foreignChat);
    }

    private Component buildServerJoinMessage(Player p, RegisteredServer original, RegisteredServer to){
        Component alias = config.serverAlias.get(to.getServerInfo().getName());
        Context ctx = rootCtx.newContext()
            .local(FormatVar.CURRENT_SERVER_NAME, to.getServerInfo().getName())
            .local(FormatVar.CURRENT_SERVER_ALIAS, alias)
            .local(FormatVar.PLAYER_NAME, p.getUsername())
            .local(FormatVar.PLAYER_UUID, p.getUsername().toString())
            .local(FormatVar.PLAYER_PING, Long.toString(p.getPing()));

        if (original != null){
            alias = config.serverAlias.get(original.getServerInfo().getName());
            return ctx
                .local(FormatVar.PREVIOUS_SERVER_NAME, original.getServerInfo().getName())
                .local(FormatVar.PREVIOUS_SERVER_ALIAS, alias)
                .format(config.formats.serverJoin);
        }
        else {
            return ctx.format(config.formats.proxyJoin);
        }
    }

    public Component buildServerList(String senderName){
        Context ctx = rootCtx.newContext()
            .local(FormatVar.PLAYER_NAME, senderName);
        TextComponent.Builder builder = TextComponent.builder().append(ctx.format(config.formats.serverList.title));
        for (RegisteredServer s: server.getAllServers()){
            Component alias = config.serverAlias.get(s.getServerInfo().getName());
            ctx.local(FormatVar.CURRENT_SERVER_NAME, s.getServerInfo().getName())
                .local(FormatVar.CURRENT_SERVER_ALIAS, alias);
            
            TextComponent.Builder pbuilder = TextComponent.builder();
            Collection<Player> players = s.getPlayersConnected();
            for (Player p: players){
                pbuilder.append(
                    ctx.player(p).format(config.formats.serverList.playerItem)
                );
            }
            if (players.size() > 0){
                ctx.local(FormatVar.PLAYER_LIST, pbuilder.build());
            }
            else {
                ctx.local(FormatVar.PLAYER_LIST, ctx.format(config.formats.serverList.emptyPlayerList));
            }
                
            builder
                .append(ctx.format(config.formats.serverList.serverItem));
        }
        return builder.build();
    }
}