package fr.multimc.api.spigot.worlds.locations.zones.enums;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public interface ZoneListener {
    void onPlayerEnter(PlayerMoveEvent e);
    void onEntityEnter(EntityMoveEvent e);
    void onPlayerExit(PlayerMoveEvent e);
    void onEntityExit(EntityMoveEvent e);
}
