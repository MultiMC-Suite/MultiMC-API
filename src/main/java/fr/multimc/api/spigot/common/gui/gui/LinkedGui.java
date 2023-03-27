package fr.multimc.api.spigot.common.gui.gui;

import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.gui.GuiView;
import fr.multimc.api.spigot.common.gui.enums.GuiSize;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class LinkedGui extends AbstractGui{

    private final Map<String, GuiView> views;

    public LinkedGui(@NotNull final Plugin plugin, @NotNull final Component title, @NotNull final GuiSize size, @NotNull final GuiView defaultView) {
        super(plugin, title, size, defaultView);
        this.views = new HashMap<>();
        this.addView("default", defaultView);
    }

    public void addView(@NotNull final String viewId, @NotNull final GuiView view){
        this.views.put(viewId, view);
    }

    public void removeView(@NotNull final String viewId){
        this.views.remove(viewId);
    }

    public void changeView(@NotNull final String viewId){
        if(!this.views.containsKey(viewId))
            throw new IllegalArgumentException("The view " + viewId + " doesn't exist in this gui");
        this.setView(this.views.get(viewId));
        this.render();
    }

    @Nullable
    public GuiView getView(@NotNull final String viewId) {
        return this.views.get(viewId);
    }

    @Override
    public void onInventoryClick(final int slot, @NotNull final MmcPlayer mmcPlayer) {

    }
}
