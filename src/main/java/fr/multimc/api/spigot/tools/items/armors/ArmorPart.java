package fr.multimc.api.spigot.tools.items.armors;

import org.bukkit.Material;

@SuppressWarnings("unused")
public enum ArmorPart {
    HELMET(Material.LEATHER_HELMET),
    CHEST_PLATE(Material.LEATHER_CHESTPLATE),
    LEGGINGS(Material.LEATHER_LEGGINGS),
    BOOTS(Material.LEATHER_BOOTS);

    private final Material material;

    ArmorPart(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}
