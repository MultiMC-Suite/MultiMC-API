package fr.multimc.api.spigot.managers.teams;

import fr.multimc.api.commons.tools.compares.StringFormatter;
import fr.multimc.api.commons.tools.enums.Status;
import fr.multimc.api.commons.tools.status.SameValue;
import fr.multimc.api.commons.tools.status.Success;
import fr.multimc.api.commons.tools.status.Error;
import fr.multimc.api.spigot.tools.chat.ClickableMessageBuilder;
import fr.multimc.api.spigot.tools.chat.TextBuilder;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.players.PlayerSpeed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"unused", "ConstantConditions", "UnusedReturnValue"})
public class APIPlayer {
    private final UUID uuid;
    private final String name;

    public APIPlayer(@Nonnull UUID uuid){
        this.uuid = uuid;
        this.name = this.fetchName();
    }

    public APIPlayer(@Nonnull String name){
        this.name = name;
        this.uuid = this.fetchUUID();
    }

    public APIPlayer(@Nonnull Player player){
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    // SETTERS & FUNCTIONS \\
    @Nonnull
    public Status setGameMode(@Nonnull GameMode mode) {
        if (this.isOnline()) return new Error("%s is not online!", this.name);
        if (this.getPlayer().getGameMode().equals(mode)) return new SameValue("%s's game mode is already %s.", this.name, StringFormatter.capitalize(mode.name()));
        this.getPlayer().setGameMode(mode);
        return new Success("%s's game mode has been updated to %s.", this.name, StringFormatter.capitalize(mode.name()));
    }

    @Nonnull
    public Status setSpeed(@Nullable PlayerSpeed speed) {
        if (this.isFlying()) return this.setFlySpeed(speed);
        return this.setWalkSpeed(speed);
    }

    @Nonnull
    public Status setWalkSpeed(@Nullable PlayerSpeed speed) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);
        if (this.getWalkSpeed().equals(speed)) return new SameValue("%s's walk speed level is already set to %s.", this.name, StringFormatter.capitalize(StringFormatter.reduce(this.getWalkSpeed().name().split("_"))));
        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;
        this.getPlayer().setWalkSpeed(level.getWalkLevel());
        return new Success("%s's walk speed level has been updated to %s.");
    }

    @Nonnull
    public Status setFlySpeed(@Nullable PlayerSpeed speed) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);
        if (this.getFlySpeed().equals(speed)) return new SameValue("%s's fly speed level is already set to %s.", this.name, StringFormatter.capitalize(StringFormatter.reduce(this.getWalkSpeed().name().split("_"))));
        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;
        this.getPlayer().setFlySpeed(level.getFlyLevel());
        return new Success("%s's fly speed level has been updated to %s.");
    }

    @Nonnull
    public Status teleport(@Nonnull APIPlayer target) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);
        if (!target.isOnline()) return new Error("%s is not online!", target.getName());

        this.getPlayer().teleport(target.getPlayer());
        return new Success("%s has been teleported to %s.", this.name, target.getName());
    }

    @Nonnull
    public Status teleport(@Nonnull Location target) {
        return this.teleport(target, false);
    }

    @Nonnull
    public Status teleport(@Nonnull Location location, boolean toCenter) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);

        Location target = toCenter ? location.getBlock().getLocation().clone().add(.5, 0, .5) : location;
        this.getPlayer().teleport(target);
        return new Success("%s has been teleported to %d, %d, %d.", this.name, target.getX(), target.getY(), target.getZ());
    }

    @Nonnull
    public Status teleport(@Nonnull RelativeLocation location) {
        return this.teleport(location, false);
    }

    @Nonnull
    public Status teleport(@Nonnull RelativeLocation location, boolean toCenter) {
        return this.teleport(location.toAbsolute(this.getPlayer().getLocation()), toCenter);
    }

    @Nonnull
    public Status sendMessage(@Nonnull String message) {
        return this.sendMessage(new TextBuilder(message).build());
    }

    @Nonnull
    public Status sendMessage(@Nonnull Component component) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);
        this.getPlayer().sendMessage(component);
        return new Success("%s received the message \"%s\".", this.name, PlainTextComponentSerializer.plainText().serialize(component));
    }

    @Nonnull
    public Status sendMessage(@Nonnull ClickableMessageBuilder component) {
        return this.sendMessage(component.build());
    }

    @Nonnull
    public Status sendActionBar(@Nonnull String text) {
        return this.sendActionBar(new TextBuilder(text).build());
    }

    @Nonnull
    public Status sendActionBar(@Nonnull Component component) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);
        this.getPlayer().sendActionBar(component);
        return new Success("%s received the action bar \"%s\".", this.name, PlainTextComponentSerializer.plainText().serialize(component));
    }

    @Nonnull
    public Status sendTitle(@Nullable String title, @Nullable String subtitle) {
        return this.sendTitle(title, subtitle, Duration.ofSeconds(1L));
    }

    @Nonnull
    public Status sendTitle(@Nullable Component title, @Nullable Component subtitle) {
        return this.sendTitle(title, subtitle, Duration.ofSeconds(1L));
    }

    @Nonnull
    public Status sendTitle(@Nullable String title, @Nullable String subtitle, @Nonnull Duration stay) {
        return this.sendTitle(title, subtitle, Duration.ZERO, stay, Duration.ZERO);
    }

    @Nonnull
    public Status sendTitle(@Nullable Component title, @Nullable Component subtitle, @Nonnull Duration stay) {
        return this.sendTitle(title, subtitle, Duration.ZERO, stay, Duration.ZERO);
    }

    @Nonnull
    public Status sendTitle(@Nullable String title, @Nullable String subtitle, @Nonnull Duration fade, @Nonnull Duration stay) {
        return this.sendTitle(title, subtitle, fade, stay, fade);
    }

    @Nonnull
    public Status sendTitle(@Nullable Component title, @Nullable Component subtitle, @Nonnull Duration fade, @Nonnull Duration stay) {
        return this.sendTitle(title, subtitle, fade, stay, fade);
    }

    @Nonnull
    public Status sendTitle(@Nullable String title, @Nullable String subtitle, @Nonnull Duration fadeIn, @Nonnull Duration stay, @Nonnull Duration fadeOut) {
        return this.sendTitle(title, subtitle, Title.Times.times(fadeIn, stay, fadeOut));
    }

    @Nonnull
    public Status sendTitle(@Nullable Component title, @Nullable Component subtitle, @Nonnull Duration fadeIn, @Nonnull Duration stay, @Nonnull Duration fadeOut) {
        return this.sendTitle(title, subtitle, Title.Times.times(fadeIn, stay, fadeOut));
    }

    @Nonnull
    private Status sendTitle(@Nullable String title, @Nullable String subtitle, @Nullable Title.Times times) {
        return this.sendTitle(Objects.isNull(title) ? null : new TextBuilder(title).build(), Objects.isNull(subtitle) ? null : new TextBuilder(subtitle).build(), times);
    }

    @Nonnull
    private Status sendTitle(@Nullable Component title, @Nonnull Component subtitle, @Nullable Title.Times times) {
        if (!this.isOnline()) return new Error("%s is not online!", this.name);

        this.getPlayer().showTitle(Title.title(new TextBuilder(title).build(), new TextBuilder(subtitle).build(), times));
        return new Success("%s received the title.", this.name);
    }

    @Nullable
    public PlayerInventory getInventory() {
        return this.isOnline() ? this.getPlayer().getInventory() : null;
    }

    // CHECKS \\
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof APIPlayer player){
            return player.getUUID() == this.uuid;
        }
        return false;
    }

    public boolean isOnline() {
        return this.getPlayer() != null;
    }

    public boolean isFlying() {
        return this.isOnline() && this.getPlayer().isFlying();
    }

    // GETTERS \\
    private String fetchName(){
        return Bukkit.getOfflinePlayer(this.uuid).getName();
    }

    private UUID fetchUUID(){
        return Bukkit.getOfflinePlayer(this.name).getUniqueId();
    }

    @Nullable
    public Player getPlayer(){
        return Bukkit.getPlayer(this.uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName(){
        return this.name;
    }

    @Nullable
    public String getNickName() {
        return this.isOnline() ? PlainTextComponentSerializer.plainText().serialize(this.getPlayer().displayName()) : null;
    }

    @NotNull
    public PlayerSpeed getWalkSpeed() {
        return PlayerSpeed.fromWalkSpeed(this.getPlayer().getWalkSpeed()).orElse(PlayerSpeed.LEVEL_1);
    }

    @Nonnull
    public PlayerSpeed getFlySpeed() {
        return PlayerSpeed.fromFlySpeed(this.getPlayer().getFlySpeed()).orElse(PlayerSpeed.LEVEL_1);
    }
}
