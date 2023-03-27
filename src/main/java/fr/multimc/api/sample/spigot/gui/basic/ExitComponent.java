package fr.multimc.api.sample.spigot.gui.basic;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.gui.components.AbstractComponent;
import fr.multimc.api.spigot.gui.gui.AbstractGui;
import fr.multimc.api.spigot.tools.builders.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ExitComponent extends AbstractComponent {

    public ExitComponent() {
        super(new ItemBuilder(Material.BARRIER).setName(Component.text("Exit").color(NamedTextColor.RED)).build());
    }

    @Override
    public void onClicked(@NotNull final AbstractGui gui, @NotNull final MmcPlayer mmcPlayer) {
        gui.closeInventory(mmcPlayer);
    }
}
