package com.hadroncfy.multichat.componentrenderer;

import java.util.Optional;

import javax.swing.text.JTextComponent.KeyBinding;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;


// Copied a lot of code from https://github.com/KyoriPowered/text/blob/master/api/src/main/java/net/kyori/text/renderer/FriendlyComponentRenderer.java
// I really dont know a better choice...
public abstract class AbstractTextComponentRenderer<C> implements ComponentRenderer<C> {

    protected abstract @NonNull ComponentBuilder<?, ?> renderText(String txt, @NonNull C c);

    private String renderToString(String txt, @NonNull C c){
        ComponentBuilder<?, ?> cb = renderText(txt, c);
        return LegacyComponentSerializer.legacyAmpersand().serialize(cb.build());
    }

    @Override
    public @NonNull Component render(final @NonNull Component component, final @NonNull C context) {
        if(component instanceof TranslatableComponent) {
            return this.render((TranslatableComponent) component, context);
        } else if(component instanceof TextComponent) {
            // final TextComponent.Builder builder = TextComponent.builder(((TextComponent) component).content());
            final ComponentBuilder<?, ?> builder = renderText(((TextComponent) component).content(), context);
            return this.deepRender(component, builder, context).build();
        } else if(component instanceof KeybindComponent) {
            // final KeybindComponent.Builder builder = KeybindComponent.builder(((KeybindComponent) component).keybind());
            final KeybindComponent.Builder builder = Component.keybind(((KeybindComponent) component).keybind()).toBuilder();
            return this.deepRender(component, builder, context).build();
        } else if(component instanceof ScoreComponent) {
            final ScoreComponent sc = (ScoreComponent) component;
            final ScoreComponent.Builder builder = Component.score()
            .name(sc.name())
            .objective(sc.objective());
            return this.deepRender(component, builder, context).build();
        } else if(component instanceof SelectorComponent) {
            final SelectorComponent.Builder builder = Component.selector(((SelectorComponent) component).pattern()).toBuilder();
            return this.deepRender(component, builder, context).build();
        } else {
            return component;
        }
    }

    private <B extends ComponentBuilder<?, ?>> B deepRender(final Component component, final B builder, final C context) {
        this.mergeStyle(component, builder, context);
        component.children().forEach(child -> builder.append(this.render(child, context)));
        return builder;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <B extends ComponentBuilder<?, ?>> void mergeStyle(final Component component, final B builder, final C context) {
        builder.mergeStyle(component);
        HoverEvent hev = component.hoverEvent();
        if (hev != null) {
            if (hev.action() == HoverEvent.Action.SHOW_TEXT) {
                builder.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, this.render((@NonNull Component) hev.value(), context)));
            } else {
                builder.hoverEvent(HoverEvent.hoverEvent(hev.action(), hev.value()));
            }
        }
        ClickEvent cl = component.clickEvent();
        if (cl != null){
            builder.clickEvent(ClickEvent.clickEvent(cl.action(), renderToString(cl.value(), context)));
        }
    }
}