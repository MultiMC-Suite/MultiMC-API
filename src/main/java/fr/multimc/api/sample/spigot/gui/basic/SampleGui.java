package fr.multimc.api.sample.spigot.gui.basic;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.gui.GuiView;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import fr.multimc.api.spigot.gui.enums.Side;
import fr.multimc.api.spigot.gui.gui.AbstractGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SampleGui extends AbstractGui {

    public SampleGui(@NotNull final Plugin plugin) {
        super(plugin, Component.text("Sample").color(NamedTextColor.GOLD), GuiSize.DOUBLE_CHEST);
        GuiView view = this.getView();
        view.fill(Material.GRAY_STAINED_GLASS_PANE);
        view.fill(Material.RED_STAINED_GLASS_PANE, this.getBorder(Side.TOP));
        view.fill(Material.GREEN_STAINED_GLASS_PANE, this.getBorder(Side.BOTTOM));
        view.fill(Material.BLUE_STAINED_GLASS_PANE, this.getBorder(Side.LEFT));
        view.fill(Material.PURPLE_STAINED_GLASS_PANE, this.getBorder(Side.RIGHT));
        view.setComponent(this.getLastSlot(), new ExitComponent());
        this.render();
    }

    @Override
    public void onInventoryClick(int slot, @NotNull MmcPlayer mmcPlayer) {

    }
}
