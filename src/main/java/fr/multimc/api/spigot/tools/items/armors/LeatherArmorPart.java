package fr.multimc.api.spigot.tools.items.armors;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum LeatherArmorPart {
    HELMET(Material.LEATHER_HELMET),
    CHEST_PLATE(Material.LEATHER_CHESTPLATE),
    LEGGINGS(Material.LEATHER_LEGGINGS),
    BOOTS(Material.LEATHER_BOOTS);

    private final Material material;

    LeatherArmorPart(@NotNull Material material) {
        this.material = material;
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }
}
