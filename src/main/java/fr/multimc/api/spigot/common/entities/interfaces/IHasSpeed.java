package fr.multimc.api.spigot.common.entities.interfaces;

import fr.multimc.api.spigot.common.entities.player.PlayerSpeed;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface IHasSpeed {
    boolean setSpeed(@Nullable PlayerSpeed speed);
    boolean setWalkSpeed(@Nullable PlayerSpeed speed);
    boolean setFlySpeed(@Nullable PlayerSpeed speed);
}
