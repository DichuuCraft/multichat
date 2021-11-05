package com.hadroncfy.multichat.componentrenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;


public class FormatBuilder extends AbstractTextComponentRenderer<Context> {
    private static final Pattern VAL_REGEX = Pattern.compile("\\$[a-zA-Z0-9_\\-]+");

    FormatBuilder(){
    }

    private List<Component> renderString(String s, @NonNull Context ctx){
        Matcher m = VAL_REGEX.matcher(s);
        List<Component> ret = new ArrayList<>();
        int lastIndex = 0;
        while (m.find()){
            if (lastIndex != m.start())
                ret.add(Component.text(s.substring(lastIndex, m.start())));

            String name = m.group();
            lastIndex = m.start() + name.length();
            Component val = null;
            if (name.equals("$$")){
                val = Component.text("$$");
            } else {
                try {
                    FormatVar f = FormatVar.valueOf(name.substring(1));
                    Component v = ctx.get(f);
                    if (v != null){
                        val = render(v, ctx.newContext());
                    } else {
                        val = Component.text(name);
                    }
                } catch(IllegalArgumentException e){
                    val = Component.text(name);
                }
            }
            ret.add(val);
        }
        if (lastIndex < s.length()){
            ret.add(Component.text(s.substring(lastIndex)));
        }
        return ret;
    }

    @Override
    protected @NonNull ComponentBuilder<?, ?> renderText(String content, @NonNull Context ctx) {
        ComponentBuilder<?, ?> builder = Component.text();
        renderString(content, ctx).forEach(builder::append);
        return builder;
    }
}