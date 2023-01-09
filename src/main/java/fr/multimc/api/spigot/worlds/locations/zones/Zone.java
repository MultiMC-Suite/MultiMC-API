package fr.multimc.api.spigot.worlds.locations.zones;

import fr.multimc.api.commons.tools.formatters.MathNumber;
import fr.multimc.api.spigot.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.worlds.locations.zones.enums.IZoneCallback;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Classe représentant une zone 3D définie par deux emplacements.
 * Cette classe implémente l'interface {@link Listener} pour permettre la gestion des événements de mouvement de joueurs et d'entités dans la zone.
 *
 * @author Xen0Xys
 */
@SuppressWarnings("unused")
public class Zone implements Listener {

    private final double minX, maxX, minY, maxY, minZ, maxZ;
    /** Callback appelé lorsque qu'un joueur ou une entité entre ou sort de la zone */
    private IZoneCallback callback;

    /**
     * Crée une nouvelle zone à partir de deux emplacements.
     *
     * @param location1 {@link Location} Premier emplacement de la zone
     * @param location2 {@link Location} Second emplacement de la zone
     * @param plugin {@link Plugin} enregistrant l'écouteur d'événements (optionnel, peut être null)
     * @param callback {@link IZoneCallback} appelé lorsque qu'un joueur ou une entité entre ou sort de la zone (optionnel, peut être null)
     */
    public Zone(@Nonnull Location location1, @Nonnull Location location2, @Nullable Plugin plugin, @Nullable IZoneCallback callback) {
        this.minX = Math.min(location1.getBlockX(), location2.getBlockX());
        this.minY = Math.min(location1.getBlockY(), location2.getBlockY());
        this.minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        this.maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        this.maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        this.maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
        if(Objects.nonNull(plugin) && Objects.nonNull(callback)) {
            this.callback = callback;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    /**
     * Crée une nouvelle zone à partir de deux emplacements.
     *
     * @param location1 {@link Location} Premier emplacement de la zone
     * @param location2 {@link Location} Second emplacement de la zone
     */
    public Zone(@Nonnull Location location1, @Nonnull Location location2) {
        this(location1, location2, null, null);
    }

    /**
     * Crée une nouvelle zone à partir de deux emplacements relatifs à un emplacement central.
     *
     * @param center {@link Location} Emplacement central de la zone
     * @param location1 {@link RelativeLocation} Premier emplacement relatif de la zone
     * @param location2 {@link RelativeLocation} Second emplacement relatif de la zone
     * @param plugin {@link Plugin} enregistrant l'écouteur d'événements (optionnel, peut être null)
     * @param callback {@link IZoneCallback}  appelé lorsque qu'un joueur ou une entité entre ou sort de la zone (optionnel, peut être null)
     */
    public Zone(@Nonnull Location center, @Nonnull RelativeLocation location1, @Nonnull RelativeLocation location2, @Nullable Plugin plugin, @Nullable IZoneCallback callback) {
        this(location1.toAbsolute(center), location2.toAbsolute(center), plugin, callback);
    }

    /**
     * Crée une nouvelle zone à partir de deux emplacements relatifs à un emplacement central.
     *
     * @param center {@link Location} Emplacement central de la zone
     * @param location1 {@link RelativeLocation} Premier emplacement relatif de la zone
     * @param location2 {@link RelativeLocation} Second emplacement relatif de la zone
     */
    public Zone(@Nonnull Location center, @Nonnull RelativeLocation location1, @Nonnull RelativeLocation location2) {
        this(location1.toAbsolute(center), location2.toAbsolute(center), null, null);
    }

    /**
     * Vérifie si le joueur est présent dans la zone.
     *
     * @param player {@link Player} Joueur à vérifier
     * @return true si le joueur est présent dans la zone, false sinon
     */
    public boolean isIn(@Nonnull Player player) {
        return MathNumber.isDoubleBetween(player.getLocation().getBlockX(), minX, maxX)
                && MathNumber.isDoubleBetween(player.getLocation().getBlockY(), minY, maxY)
                && MathNumber.isDoubleBetween(player.getLocation().getBlockZ(), minZ, maxZ);
    }

    /**
     * Vérifie si l'emplacement est présent dans la zone.
     *
     * @param location {@link Location} Emplacement à vérifier
     * @return true si l'emplacement est présent dans la zone, false sinon
     */
    public boolean isIn(@Nonnull Location location) {
        return MathNumber.isDoubleBetween(location.getX(), minX, maxX)
                && MathNumber.isDoubleBetween(location.getY(), minY, maxY)
                && MathNumber.isDoubleBetween(location.getZ(), minZ, maxZ);
    }

    /**
     * Gestionnaire d'événements pour les déplacements de joueurs.
     * Appelle la méthode {@link IZoneCallback#onEnter(PlayerMoveEvent)} ou {@link IZoneCallback#onExit(PlayerMoveEvent)} si le joueur entre ou sort de la zone.
     *
     * @param e {@link PlayerMoveEvent} Événement de déplacement de joueur
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        if(!this.isIn(e.getFrom()) && this.isIn(e.getTo()))
            this.callback.onEnter(e);
        else if(this.isIn(e.getFrom()) && !this.isIn(e.getTo()))
            this.callback.onExit(e);
    }

    /**
     * Gestionnaire d'événements pour les déplacements d'entités.
     * Appelle la méthode {@link IZoneCallback#onEnter(EntityMoveEvent)} ou {@link IZoneCallback#onExit(EntityMoveEvent)} si l'entité entre ou sort de la zone.
     *
     * @param e {@link EntityMoveEvent} Événement de déplacement d'entité
     */
    @EventHandler
    private void onEntityMove(EntityMoveEvent e){
        if(!this.isIn(e.getFrom()) && this.isIn(e.getTo()))
            this.callback.onEnter(e);
        else if(this.isIn(e.getFrom()) && !this.isIn(e.getTo()))
            this.callback.onExit(e);
    }
}
