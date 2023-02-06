package fr.multimc.api.sample.spigot.gui;

import fr.multimc.api.spigot.gui.AbstractGui;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SampleGui extends AbstractGui {

    public SampleGui(@NotNull Plugin plugin, @NotNull Component title, @NotNull GuiSize size) {
        super(plugin, title, size);
    }

}
