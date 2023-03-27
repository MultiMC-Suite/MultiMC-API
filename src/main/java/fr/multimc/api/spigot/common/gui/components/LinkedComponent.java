package fr.multimc.api.spigot.common.gui.components;

import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.gui.gui.AbstractGui;
import fr.multimc.api.spigot.common.gui.gui.LinkedGui;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LinkedComponent extends AbstractComponent{

    private final LinkedGui linkedGui;
    private final String targetId;

    public LinkedComponent(@NotNull final ItemStack itemStack, @NotNull final LinkedGui linkedGui, @NotNull final String targetId) {
        super(itemStack);
        this.linkedGui = linkedGui;
        this.targetId = targetId;
    }

    @Override
    public void onClicked(@NotNull final AbstractGui gui, @NotNull final MmcPlayer mmcPlayer) {
        this.linkedGui.changeView(this.targetId);
    }
}
