package fr.multimc.api.spigot.tools.players;

import fr.multimc.api.commons.tools.enums.Status;
import fr.multimc.api.commons.tools.status.*;
import fr.multimc.api.spigot.tools.chat.ClickableMessageBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class PlayerUtils {
    private final Player player;

    /**
     *
     * @param player
     */
    public PlayerUtils(@Nonnull Player player) {
        this.player = player;
    }

    /**
     *
     * @param mode
     * @return
     */
    public Status setGameMode(@Nonnull GameMode mode) {
        if (this.player.getGameMode().equals(mode)) return new SameValue(String.format("%s's game mode is already %s!", this.getNickName(), mode.name()));
        this.player.setGameMode(mode);
        return new Success(String.format("Game mode %s set to %s.", mode.name(), this.getNickName()));
    }

    /**
     *
     * @param speed
     * @return
     */
    public Status setSpeedLevel(@Nullable PlayerSpeed speed) {
        if (this.isFlying()) return this.setFlySpeed(speed);
        return this.setWalkSpeed(speed);
    }

    /**
     *
     * @param speed
     * @return
     */
    public Status setWalkSpeed(@Nullable PlayerSpeed speed) {
        if (this.getWalkSpeed().equals(speed)) return new SameValue(String.format("%s's walk speed level is already %s!", this.getNickName(), this.getWalkSpeed()));

        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;

        player.setWalkSpeed(level.getWalkLevel());
        return new Success(String.format("%s's walk speed level has been updated to %s.", this.getNickName(), level.name()));
    }

    /**
     *
     * @param speed
     * @return
     */
    public Status setFlySpeed(@Nullable PlayerSpeed speed) {
        if (this.getFlySpeed().equals(speed)) return new SameValue(String.format("%s's fly speed level is already %s!", this.getNickName(), this.getFlySpeed()));

        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;

        player.setFlySpeed(level.getFlyLevel());
        return new Success(String.format("%s's fly speed level has been updated to %s.", this.getNickName(), level.name()));
    }

    /**
     *
     * @param message
     * @return
     */
    @Deprecated
    public Status sendMessage(@Nonnull String message) {
        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return new Success(String.format("The message '%s' has been sent to %s", message, this.getNickName()));
    }

    /**
     *
     * @param component
     * @return
     */
    @Deprecated
    public Status sendMessage(@Nonnull TextComponent component) {
        player.sendMessage(component);
        return new Success(String.format("The message '%s' has been sent to %s.", component.getText(), this.getNickName()));
    }

    /**
     *
     * @param component
     * @return
     */
    @Deprecated
    public Status sendMessage(@Nonnull ClickableMessageBuilder component) {
        return this.sendMessage(component.build());
    }

    /// GETTERS \\\
    /**
     *
     * @return
     */
    public boolean isFlying() {
        return this.player.isFlying();
    }

    /**
     *
     * @return
     */
    public PlayerSpeed getWalkSpeed() {
        Optional<PlayerSpeed> speed = PlayerSpeed.fromWalkSpeed(player.getWalkSpeed());
        assert speed.isPresent();
        return speed.get();
    }

    /**
     *
     * @return
     */
    public PlayerSpeed getFlySpeed() {
        Optional<PlayerSpeed> speed = PlayerSpeed.fromFlySpeed(player.getFlySpeed());
        assert speed.isPresent();
        return speed.get();
    }

    /**
     *
     * @return
     */
    @Deprecated
    public String getNickName() {
        return this.player.getDisplayName();
    }
}
