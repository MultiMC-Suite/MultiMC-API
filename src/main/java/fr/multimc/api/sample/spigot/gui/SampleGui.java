package fr.multimc.api.sample.spigot.gui;

import fr.multimc.api.spigot.gui.AbstractGui;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import fr.multimc.api.spigot.gui.enums.Side;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SampleGui extends AbstractGui {

    public SampleGui(@NotNull final Plugin plugin) {
        super(plugin, Component.text("Sample").color(NamedTextColor.GOLD), GuiSize.DOUBLE_CHEST);
        this.fill(Material.GRAY_STAINED_GLASS_PANE);
        this.fill(Material.RED_STAINED_GLASS_PANE, this.getBorder(Side.TOP));
        this.fill(Material.GREEN_STAINED_GLASS_PANE, this.getBorder(Side.BOTTOM));
        this.fill(Material.BLUE_STAINED_GLASS_PANE, this.getBorder(Side.LEFT));
        this.fill(Material.PURPLE_STAINED_GLASS_PANE, this.getBorder(Side.RIGHT));
        this.addComponent(this.getLastSlot(), new ExitComponent());
    }

}
