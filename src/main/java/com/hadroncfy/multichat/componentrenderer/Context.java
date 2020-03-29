package com.hadroncfy.multichat.componentrenderer;

import java.util.EnumMap;
import java.util.Map;

import com.velocitypowered.api.proxy.Player;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;

public class Context {
    private Map<FormatVar, Component> global = new EnumMap<>(FormatVar.class), local = new EnumMap<>(FormatVar.class);
    
    public Context newContext(){
        Context ctx = new Context();
        global.putAll(ctx.global);
        return ctx;
    }
    public Context local(FormatVar name, Component val){
        local.put(name, val);
        return this;
    }
    public Context local(FormatVar name, String val){
        local.put(name, TextComponent.of(val));
        return this;
    }
    public Context global(FormatVar name, Component val){
        global.put(name, val);
        return this;
    }
    public Context player(Player p){
        local(FormatVar.PLAYER_NAME, TextComponent.of(p.getUsername()));
        local(FormatVar.PLAYER_UUID, TextComponent.of(p.getUsername().toString()));
        local(FormatVar.PLAYER_PING, TextComponent.of(p.getPing()));
        return this;
    }
    Component get(FormatVar name){
        Component v = local.get(name);
        if (v != null){
            return v;
        }
        return global.get(name);
    }
    public Component format(Component c){
        return new FormatBuilder().render(c, this);
    }

}