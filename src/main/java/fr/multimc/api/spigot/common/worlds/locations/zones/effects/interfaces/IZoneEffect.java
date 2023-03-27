package fr.multimc.api.spigot.common.worlds.locations.zones.effects.interfaces;

import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import org.bukkit.entity.Entity;

/**
 * An interface for a zone effect.
 * @author Xen0Xys
 */
public interface IZoneEffect {
    /**
     * Applies the effect to the given player.
     *
     * @param player {@link MmcPlayer} to apply the effect to
     */
    void applyEffect(MmcPlayer player);

    /**
     * Removes the effect from the given player.
     *
     * @param player {@link MmcPlayer} to remove the effect from
     */
    void removeEffect(MmcPlayer player);

    /**
     * Applies the effect to the given entity.
     *
     * @param entity {@link Entity} to apply the effect to
     */
    void applyEffect(Entity entity);

    /**
     * Removes the effect from the given entity.
     *
     * @param entity {@link Entity} to remove the effect from
     */
    void removeEffect(Entity entity);
}
