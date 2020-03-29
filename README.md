# MultiChat
用于Velocity的多服务器聊天插件。

Simple multi-server chat plugin for Velocity. It forwards chat messages from one server to the other servers in the proxy in a customizable format.

## 配置实例 / Example Configuration
`config.json`:
```json
{
  "serverAlias": {
    "survivaux": {
      "text": "[生存界]",
      "color": "green",
      "hoverEvent": {
        "action":"show_text",
        "value": [
          "Name: ", { "text": "survivaux\n", "color": "dark_green" },
          { "text": "Click to join this server", "color": "gray", "italic": true }
        ]
      },
      "clickEvent": {
        "action": "run_command",
        "value": "/server survivaux"
      }
    }
  },
  "sendHelloMessage": true,
  "formats": {
    "foreignChat": [
      "$CURRENT_SERVER_ALIAS <",
      {
        "text": "$PLAYER_NAME",
        "hoverEvent": {
          "action": "show_text",
          "value": [
            "Type: ",
            {
              "text": "minecraft:player\n",
              "italic": true,
              "color": "dark_green"
            },
            "UUID: ",
            {
              "text": "$PLAYER_UUID",
              "italic": true,
              "color": "dark_green"
            }
          ]
        },
        "clickEvent": {
          "action": "suggest_command",
          "value": "/t $PLAYER_NAME"
        }
      },
      "> $MESSAGE"
    ],
    "serverJoin": "$PLAYER_NAME left $PREVIOUS_SERVER_ALIAS, joined $CURRENT_SERVER_ALIAS",
    "serverList": {
      "title": "Welcome $PLAYER_NAME to server! Server list:\n",
      "serverItem": "$CURRENT_SERVER_ALIAS: $PLAYER_LIST\n",
      "emptyPlayerList": { "text": "(No players)", "italic": true, "color": "dark_gray" },
      "playerItem": {
        "text": "$PLAYER_NAME ", "color": "dark_aqua",
        "hoverEvent": {
          "action": "show_text",
          "value": [
            "Ping: ", { "text": "$PLAYER_PING ms", "italic": true, "color": "gray" }
          ]
        },
        "clickEvent": {
          "action": "suggest_command",
          "value": "/t $PLAYER_NAME"
        }
      }
    }
  }
}

```
