package com.hadroncfy.multichat.componentrenderer;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.kyori.text.Component;
import net.kyori.text.ComponentBuilder;
import net.kyori.text.KeybindComponent;
import net.kyori.text.ScoreComponent;
import net.kyori.text.SelectorComponent;
import net.kyori.text.TextComponent;
import net.kyori.text.TranslatableComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.renderer.ComponentRenderer;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

// Copied a lot of code from https://github.com/KyoriPowered/text/blob/master/api/src/main/java/net/kyori/text/renderer/FriendlyComponentRenderer.java
// I really dont know a better choice...
public abstract class AbstractTextComponentRenderer<C> implements ComponentRenderer<C> {

    protected abstract @NonNull ComponentBuilder<?, ?> renderText(String txt, @NonNull C c);

    private String renderToString(String txt, @NonNull C c){
        ComponentBuilder<?, ?> cb = renderText(txt, c);
        return LegacyComponentSerializer.legacy().serialize(cb.build());
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
            final KeybindComponent.Builder builder = KeybindComponent.builder(((KeybindComponent) component).keybind());
            return this.deepRender(component, builder, context).build();
        } else if(component instanceof ScoreComponent) {
            final ScoreComponent sc = (ScoreComponent) component;
            final ScoreComponent.Builder builder = ScoreComponent.builder()
            .name(sc.name())
            .objective(sc.objective())
            .value(sc.value());
            return this.deepRender(component, builder, context).build();
        } else if(component instanceof SelectorComponent) {
            final SelectorComponent.Builder builder = SelectorComponent.builder(((SelectorComponent) component).pattern());
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

    private <B extends ComponentBuilder<?, ?>> void mergeStyle(final Component component, final B builder, final C context) {
        builder.mergeColor(component);
        builder.mergeDecorations(component);
        Optional.ofNullable(component.hoverEvent()).ifPresent(hoverEvent -> {
        builder.hoverEvent(HoverEvent.of(
                hoverEvent.action(),
                this.render(hoverEvent.value(), context)
            ));
        });
        ClickEvent cl = component.clickEvent();
        if (cl != null){
            builder.clickEvent(ClickEvent.of(cl.action(), renderToString(cl.value(), context)));
        }
    }
}