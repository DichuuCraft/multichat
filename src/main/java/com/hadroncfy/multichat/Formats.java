package com.hadroncfy.multichat;

import net.kyori.adventure.text.Component;

public class Formats {
    public Component proxyJoin = Component.text("$PLAYER_NAME joined $CURRENT_SERVER_NAME");
    public Component proxyLeft = Component.text("$PLAYER_NAME left $CURRENT_SERVER_NAME");
    public Component serverJoin = Component.text("$PLAYER_NAME left $PREVIOUS_SERVER_NAME, joined $CURRENT_SERVER_NAME");
    public Component foreignChat = Component.text("* [$CURRENT_SERVER_NAME] <$PLAYER_NAME> $MESSAGE");
    // public Component serverListTitle = Component.text("Welcome $PLAYER_NAME to this server!");
    // public Component serverListItem = Component.text("$CURRENT_SERVER_NAME");
    public ServerListFormat serverList = new ServerListFormat();

    class ServerListFormat {
        public Component title = Component.text("Welcome $PLAYER_NAME to this server!");
        public Component serverItem = Component.text("$CURRENT_SERVER_NAME: $PLAYER_LIST\n");
        public Component playerItem = Component.text("$PLAYER_NAME ");
        public Component emptyPlayerList = Component.text("(No players)");
    }
}