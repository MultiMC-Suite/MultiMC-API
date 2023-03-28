package fr.multimc.api.spigot.common.tools.builders.items;

import fr.multimc.api.commons.tools.messages.components.ComponentBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Create easily an item.
 *
 * @author Loïc MAES
 * @version 1.0
 * @since 04/10/2022
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ItemBuilder {
    private final ItemStack item;

    /**
     * Create an instance of an existing item.
     * @param item Base item.
     */
    public ItemBuilder(@NotNull ItemStack item) {
        this.item = item;
    }

    /**
     * Create a new item.
     * @param material Item type.
     */
    public ItemBuilder(@NotNull Material material) {
        this.item = new ItemStack(material, 1);
    }

    /**
     * Create a new item with a different amount.
     * @param material Item type.
     * @param amount Item amount.
     */
    public ItemBuilder(@NotNull Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    /**
     * Build the modified item.
     * @return Item modified.
     */
    public ItemStack build() {
        return this.item;
    }

    /**
     * Get the item metadata.
     * @return Item metadata.
     */
    public ItemMeta getMeta() {
        return this.item.getItemMeta();
    }

    /**
     * Apply the metadata to the item.
     * @param meta Meta data to apply.
     * @return The current instance of the builder.
     */
    public ItemBuilder applyMeta(@NotNull ItemMeta meta) {
        this.item.setItemMeta(meta);
        return this;
    }

    /**
     * Set the item custom display name (colors included).
     * @param name Display name.
     * @return Current instance of the builder.
     */
    @Deprecated
    public ItemBuilder setLegacyName(@NotNull String name) {
        return this.setName(new ComponentBuilder(name).build());
    }

    public ItemBuilder setName(@NotNull Component name){
        ItemMeta meta = this.getMeta();
        meta.displayName(name);
        return this.applyMeta(meta);
    }

    /**
     * Set the item description (colors included).
     * @param lore Description lines.
     * @return Current instance of the builder.
     */
    @Deprecated
    public ItemBuilder setLegacyLore(@Nullable List<String> lore) {
        List<Component> list = new ArrayList<>();
        if (Objects.nonNull(lore)) lore.forEach(line -> list.add(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', line)).build()));
        return this.setLore(list);
    }

    public ItemBuilder setLore(@NotNull final List<Component> lore) {
        final ItemMeta meta = this.getMeta();
        meta.lore(lore);
        return this.applyMeta(meta);
    }

    /**
     * Set the item description (colors included).
     * @param lore Descriptions lines.
     * @return Current instance of the builder.
     */
    @Deprecated
    public ItemBuilder setLegacyLore(@NotNull String... lore) {
        return this.setLegacyLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(@NotNull Component... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    /**
     * Set the item's unbreakable state.
     * @param state State.
     * @return Current instance of the builder.
     */
    public ItemBuilder setUnbreakable(boolean state) {
        ItemMeta meta = this.getMeta();
        meta.setUnbreakable(state);
        return this.applyMeta(meta);
    }

    /**
     * Add some flags (tags) to the item.
     * @param flags Flags.
     * @return Current instance of the builder.
     */
    public ItemBuilder addFlags(@NotNull ItemFlag... flags) {
        ItemMeta meta = this.getMeta();
        meta.addItemFlags(flags);
        return this.applyMeta(meta);
    }

    /**
     * Remove some flags (tags) of the item.
     * @param flags Flags.
     * @return Current instance of the builder.
     */
    public ItemBuilder removeFlags(@NotNull ItemFlag... flags) {
        ItemMeta meta = this.getMeta();
        meta.removeItemFlags(flags);
        return this.applyMeta(meta);
    }

    /**
     * Enchant the item.
     * @param enchantment Enchantment.
     * @param level Level.
     * @return Current instance of the builder.
     */
    public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        ItemMeta meta = this.getMeta();
        meta.addEnchant(enchantment, level, true);
        return this.applyMeta(meta);
    }

    /**
     * Add a list of enchantments.
     * @param enchantments Enchantments list.
     * @return Current instance of the builder.
     */
    public ItemBuilder addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    /**
     * Remove an enchantment.
     * @param enchantment Enchantment.
     * @return Current instance of the builder.
     */
    public ItemBuilder removeEnchantment(@NotNull Enchantment enchantment) {
        ItemMeta meta = this.getMeta();
        meta.removeEnchant(enchantment);
        return this.applyMeta(meta);
    }

    /**
     * Remove a list of enchantments.
     * @param enchantments Enchantments list.
     * @return Current instance of the builder.
     */
    public ItemBuilder removeEnchantments(@NotNull List<Enchantment> enchantments) {
        enchantments.forEach(this::removeEnchantment);
        return this;
    }

    /**
     * Remove a list of enchantments.
     * @param enchantments Enchantments list.
     * @return Current instance of the builder.
     */
    public ItemBuilder removeEnchantments(@NotNull Enchantment... enchantments) {
        return this.removeEnchantments(Arrays.asList(enchantments));
    }

    /**
     * Un enchant the item.
     * @return Current instance of the builder.
     */
    public ItemBuilder removeAllEnchantments() {
        this.getMeta().getEnchants().keySet().forEach(this::removeEnchantment);
        return this;
    }

    /**
     * Set the item's custom model data using a texture pack.
     * @param data Custom model data.
     * @return Current instance of the builder.
     */
    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = this.getMeta();
        meta.setCustomModelData(data);
        return this.applyMeta(meta);
    }
}
