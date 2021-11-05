package com.hadroncfy.multichat.componentrenderer;

import java.util.EnumMap;
import java.util.Map;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;

public class Context {
    private final Map<FormatVar, Component> global = new EnumMap<>(FormatVar.class), local = new EnumMap<>(FormatVar.class);

    public Context newContext(){
        Context ctx = new Context();
        this.global.putAll(ctx.global);
        return ctx;
    }
    public Context local(FormatVar name, Component val){
        this.local.put(name, val);
        return this;
    }
    public Context local(FormatVar name, String val){
        this.local.put(name, Component.text(val));
        return this;
    }
    public Context global(FormatVar name, Component val){
        this.global.put(name, val);
        return this;
    }
    public Context player(Player p){
        this.local(FormatVar.PLAYER_NAME, Component.text(p.getUsername()));
        this.local(FormatVar.PLAYER_UUID, Component.text(p.getUsername().toString()));
        this.local(FormatVar.PLAYER_PING, Component.text(p.getPing()));
        return this;
    }
    Component get(FormatVar name){
        Component v = local.get(name);
        if (v != null){
            return v;
        }
        return this.global.get(name);
    }
    public Component format(Component c){
        return new FormatBuilder().render(c, this);
    }

}