package fr.multimc.api.spigot.tools.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ItemBuilder {
    private final ItemStack item;

    /**
     *
     * @param item
     */
    public ItemBuilder(@Nonnull ItemStack item) {
        this.item = item;
    }

    /**
     *
     * @param material
     */
    public ItemBuilder(@Nonnull Material material) {
        this.item = new ItemStack(material, 1);
    }

    /**
     *
     * @param material
     * @param amount
     */
    public ItemBuilder(@Nonnull Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    /**
     *
     * @return
     */
    public ItemStack build() {
        return this.item;
    }

    /**
     *
     * @return
     */
    public ItemMeta getMeta() {
        return this.item.getItemMeta();
    }

    /**
     *
     * @param meta
     * @return
     */
    public ItemBuilder applyMeta(@Nonnull ItemMeta meta) {
        this.item.setItemMeta(meta);
        return this;
    }

    /**
     *
     * @param name
     * @return
     */
    public ItemBuilder setName(@Nullable String name) {
        ItemMeta meta = this.getMeta();
        String display = Objects.isNull(name) ? null : ChatColor.translateAlternateColorCodes('&', name);
        meta.setDisplayName(display);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param lore
     * @return
     */
    public ItemBuilder setLore(@Nullable List<String> lore) {
        List<String> list = new ArrayList<>();
        lore.forEach(line -> list.add(ChatColor.translateAlternateColorCodes('&', line)));

        ItemMeta meta = this.getMeta();
        meta.setLore(list);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param lore
     * @return
     */
    public ItemBuilder setLore(@Nullable String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    /**
     *
     * @param state
     * @return
     */
    public ItemBuilder setUnbreakable(boolean state) {
        ItemMeta meta = this.getMeta();
        meta.setUnbreakable(state);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param flags
     * @return
     */
    public ItemBuilder addFlags(@Nonnull ItemFlag... flags) {
        ItemMeta meta = this.getMeta();
        meta.addItemFlags(flags);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param flags
     * @return
     */
    public ItemBuilder removeFlags(@Nonnull ItemFlag... flags) {
        ItemMeta meta = this.getMeta();
        meta.removeItemFlags(flags);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param enchantment
     * @param level
     * @return
     */
    public ItemBuilder addEnchantment(@Nonnull Enchantment enchantment, int level) {
        ItemMeta meta = this.getMeta();
        meta.addEnchant(enchantment, level, true);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param enchantments
     * @return
     */
    public ItemBuilder addEnchantments(@Nonnull Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    /**
     *
     * @param enchantment
     * @return
     */
    public ItemBuilder removeEnchantment(@Nonnull Enchantment enchantment) {
        ItemMeta meta = this.getMeta();
        meta.removeEnchant(enchantment);
        return this.applyMeta(meta);
    }

    /**
     *
     * @param enchantments
     * @return
     */
    public ItemBuilder removeEnchantments(@Nonnull List<Enchantment> enchantments) {
        enchantments.forEach(this::removeEnchantment);
        return this;
    }

    /**
     *
     * @param enchantments
     * @return
     */
    public ItemBuilder removeEnchantments(@Nonnull Enchantment... enchantments) {
        return this.removeEnchantments(Arrays.asList(enchantments));
    }

    /**
     *
     * @return
     */
    public ItemBuilder removeAllEnchantments() {
        this.getMeta().getEnchants().keySet().forEach(this::removeEnchantment);
        return this;
    }

    /**
     *
     * @param data
     * @return
     */
    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = this.getMeta();
        meta.setCustomModelData(data);
        return this.applyMeta(meta);
    }
}
