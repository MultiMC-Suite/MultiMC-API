package fr.multimc.api.spigot.tools.players;

import fr.multimc.api.commons.tools.enums.Status;
import fr.multimc.api.commons.tools.status.*;
import fr.multimc.api.spigot.tools.chat.ClickableMessageBuilder;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Simplify player's object functions and make it easier to use.
 *
 * @author Loïc MAES
 * @version 1.0
 * @since 03/10/2022
 */
public class PlayerUtils {
    private final Player player;

    /**
     * Save locally the player to update its values.
     *
     * @param player Targeted player.
     */
    public PlayerUtils(@Nonnull Player player) {
        this.player = player;
    }

    /**
     * Set the player game mode only if the targeted mode is different of his current game mode.
     *
     * @param mode Game mode to set.
     * @return Operation's status.
     */
    public Status setGameMode(@Nonnull GameMode mode) {
        if (this.player.getGameMode().equals(mode)) return new SameValue(String.format("%s's game mode is already %s!", this.getNickName(), mode.name()));
        this.player.setGameMode(mode);
        return new Success(String.format("Game mode %s set to %s.", mode.name(), this.getNickName()));
    }

    /**
     * Set the player's speed dynamically by passing a custom level.
     *
     * @param speed Speed level.
     * @return Operation's status.
     */
    public Status setSpeedLevel(@Nullable PlayerSpeed speed) {
        if (this.isFlying()) return this.setFlySpeed(speed);
        return this.setWalkSpeed(speed);
    }

    /**
     * Set the player's walk speed by passing a custom level.
     *
     * @param speed Speed level.
     * @return Operation's status.
     */
    public Status setWalkSpeed(@Nullable PlayerSpeed speed) {
        if (this.getWalkSpeed().equals(speed)) return new SameValue(String.format("%s's walk speed level is already %s!", this.getNickName(), this.getWalkSpeed()));

        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;

        player.setWalkSpeed(level.getWalkLevel());
        return new Success(String.format("%s's walk speed level has been updated to %s.", this.getNickName(), level.name()));
    }

    /**
     * Set player's fly speed by passing a custom level.
     *
     * @param speed Speed level.
     * @return Operation's status.
     */
    public Status setFlySpeed(@Nullable PlayerSpeed speed) {
        if (this.getFlySpeed().equals(speed)) return new SameValue(String.format("%s's fly speed level is already %s!", this.getNickName(), this.getFlySpeed()));

        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;

        player.setFlySpeed(level.getFlyLevel());
        return new Success(String.format("%s's fly speed level has been updated to %s.", this.getNickName(), level.name()));
    }

    /**
     * Send a custom and colored message to the player.
     *
     * @param message Message to send.
     * @return Operation's status.
     */
    public Status sendMessage(@Nonnull String message) {
        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return new Success(String.format("The message '%s' has been sent to %s", message, this.getNickName()));
    }

    /**
     * Send a custom and colored message by components (might be clickable).
     *
     * @param component Message component to send.
     * @return Operation's status.
     */
    public Status sendMessage(@Nonnull Component component) {
        player.sendMessage(component);
        return new Success(String.format("The message has been sent to %s.", this.getNickName()));
    }

    /**
     * Send a custom and colored message by builder (might be clickable).
     *
     * @param component Component to send.
     * @return Operation's status.
     */
    public Status sendMessage(@Nonnull ClickableMessageBuilder component) {
        return this.sendMessage(component.build());
    }

    /// GETTERS \\\
    /**
     * Just check if the player is currently flying.
     *
     * @return Is flying.
     */
    public boolean isFlying() {
        return this.player.isFlying();
    }

    /**
     * Get the current player's walk speed level.
     *
     * @return Walk speed level.
     */
    public PlayerSpeed getWalkSpeed() {
        Optional<PlayerSpeed> speed = PlayerSpeed.fromWalkSpeed(player.getWalkSpeed());
        assert speed.isPresent();
        return speed.get();
    }

    /**
     * Get the current player's flying speed level.
     *
     * @return Flying speed level.
     */
    public PlayerSpeed getFlySpeed() {
        Optional<PlayerSpeed> speed = PlayerSpeed.fromFlySpeed(player.getFlySpeed());
        assert speed.isPresent();
        return speed.get();
    }

    /**
     * Get the player's custom display name if present.
     *
     * @return Custom nick name.
     */
    @Deprecated
    public String getNickName() {
        return this.player.getDisplayName();
    }
}
