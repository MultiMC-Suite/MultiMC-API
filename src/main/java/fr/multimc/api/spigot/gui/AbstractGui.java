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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class AbstractGui implements Listener {

    private final GuiSize size;
    private final Inventory inventory;
    private final Map<Integer, AbstractComponent> components;

    public AbstractGui(@NotNull final Plugin plugin, @NotNull final Component title, @NotNull final GuiSize size) {
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size.getSize(), title);
        this.components = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addComponent(final int slot, @NotNull final AbstractComponent component){
        components.put(slot, component);
        inventory.setItem(slot, component.getItemStack());
    }

    public void openInventory(@NotNull final MmcPlayer mmcPlayer){
        final Player player = mmcPlayer.getPlayer();
        if(Objects.nonNull(player))
            player.openInventory(inventory);
    }

    public void fill(Material background){
        for(int i = 0; i < size.getSize(); i++)
            inventory.setItem(i, new ItemStack(background));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inventory)){
            e.setCancelled(true);
            final int slot = e.getSlot();
            final AbstractComponent component = components.get(slot);
            if(Objects.nonNull(component))
                component.onClicked(this, new MmcPlayer(e.getWhoClicked()));
        }
    }
}
