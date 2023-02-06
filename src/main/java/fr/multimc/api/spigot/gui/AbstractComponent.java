package fr.multimc.api.spigot.gui;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractComponent {

    private final ItemStack itemStack;

    public AbstractComponent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public abstract void onClicked(@NotNull final AbstractGui gui, @NotNull final MmcPlayer mmcPlayer);

}
