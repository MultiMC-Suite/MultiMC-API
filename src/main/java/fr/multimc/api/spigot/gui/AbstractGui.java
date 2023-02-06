package fr.multimc.api.spigot.gui;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractGui implements Listener {

    private final GuiSize size;
    private final Inventory inventory;

    public AbstractGui(@NotNull final Plugin plugin, @NotNull final Component title, @NotNull final GuiSize size) {
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size.getSize(), title);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openInventory(@NotNull final MmcPlayer mmcPlayer){
        final Player player = mmcPlayer.getPlayer();
        if(Objects.nonNull(player))
            player.openInventory(inventory);
    }

    public void setBackground(Material background){
        for(int i = 0; i < size.getSize(); i++)
            inventory.setItem(i, new ItemStack(background));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inventory))
            e.setCancelled(true);
    }
}
