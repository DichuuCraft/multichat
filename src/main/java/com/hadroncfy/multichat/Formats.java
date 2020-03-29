package com.hadroncfy.multichat;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;

public class Formats {
    public Component proxyJoin = TextComponent.of("$PLAYER_NAME joined $CURRENT_SERVER_NAME");
    public Component proxyLeft = TextComponent.of("$PLAYER_NAME left $CURRENT_SERVER_NAME");
    public Component serverJoin = TextComponent.of("$PLAYER_NAME left $PREVIOUS_SERVER_NAME, joined $CURRENT_SERVER_NAME");
    public Component foreignChat = TextComponent.of("* [$CURRENT_SERVER_NAME] <$PLAYER_NAME> $MESSAGE");
    // public Component serverListTitle = TextComponent.of("Welcome $PLAYER_NAME to this server!");
    // public Component serverListItem = TextComponent.of("$CURRENT_SERVER_NAME");
    public ServerListFormat serverList = new ServerListFormat();

    class ServerListFormat {
        public Component title = TextComponent.of("Welcome $PLAYER_NAME to this server!");
        public Component serverItem = TextComponent.of("$CURRENT_SERVER_NAME: $PLAYER_LIST\n");
        public Component playerItem = TextComponent.of("$PLAYER_NAME ");
        public Component emptyPlayerList = TextComponent.of("(No players)");
    }
}