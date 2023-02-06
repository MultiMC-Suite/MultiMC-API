package fr.multimc.api.spigot.entities.player;

import fr.multimc.api.commons.tools.messages.components.ComponentBuilder;
import fr.multimc.api.spigot.entities.interfaces.IHasGameMode;
import fr.multimc.api.spigot.entities.interfaces.IHasSpeed;
import fr.multimc.api.spigot.entities.interfaces.ITeleportable;
import fr.multimc.api.spigot.entities.player.enums.ClearMethod;
import fr.multimc.api.spigot.tools.builders.items.ItemBuilder;
import fr.multimc.api.spigot.worlds.locations.RelativeLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"unused", "ConstantConditions", "UnusedReturnValue"})
public class MmcPlayer implements IHasGameMode, IHasSpeed, ITeleportable {
    private final UUID uuid;
    private final String name;

    public MmcPlayer(@NotNull UUID uuid){
        this.uuid = uuid;
        this.name = this.getName();
    }

    public MmcPlayer(@NotNull String name){
        this.name = name;
        this.uuid = this.fetchUUID();
    }

    public MmcPlayer(@NotNull Player player){
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public MmcPlayer(@NotNull final HumanEntity humanEntity){
        this.uuid = humanEntity.getUniqueId();
        this.name = humanEntity.getName();
    }

    // SETTERS & FUNCTIONS \\
    @Override
    public boolean setGameMode(@NotNull GameMode mode) {
        if (!this.isOnline()) return false;
        if (this.getPlayer().getGameMode().equals(mode)) return false;
        this.getPlayer().setGameMode(mode);
        return true;
    }

    @Override
    public boolean setGameModeSync(@NotNull Plugin plugin, @NotNull GameMode mode) {
        if (!this.isOnline()) return false;
        if (this.getPlayer().getGameMode().equals(mode)) return false;
        Bukkit.getScheduler().runTask(plugin, () -> this.getPlayer().setGameMode(mode));
        return true;
    }

    @Override
    public boolean setSpeed(@Nullable PlayerSpeed speed) {
        if (this.isFlying()) return this.setFlySpeed(speed);
        return this.setWalkSpeed(speed);
    }

    @Override
    public boolean setWalkSpeed(@Nullable PlayerSpeed speed) {
        if (!this.isOnline()) return false;
        if (this.getWalkSpeed().equals(speed)) return true;
        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;
        this.getPlayer().setWalkSpeed(level.getWalkLevel());
        return true;
    }

    @Override
    public boolean setFlySpeed(@Nullable PlayerSpeed speed) {
        if (!this.isOnline()) return false;
        if (this.getFlySpeed().equals(speed)) return false;
        PlayerSpeed level = Objects.isNull(speed) ? PlayerSpeed.LEVEL_1 : speed;
        this.getPlayer().setFlySpeed(level.getFlyLevel());
        return true;
    }

    @Override
    public boolean teleportTo(@NotNull MmcPlayer target) {
        if (!this.isOnline()) return false;
        if (!target.isOnline()) return false;

        this.getPlayer().teleport(target.getPlayer());
        return true;
    }

    @Override
    public boolean teleportRelative(@NotNull RelativeLocation location) {
        return this.teleportRelative(location, false);
    }

    @Override
    public boolean teleportRelative(@NotNull RelativeLocation location, boolean center) {
        return this.teleport(location.toAbsolute(this.getPlayer().getLocation()), center);
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return this.teleport(location, false);
    }

    @Override
    public boolean teleport(@NotNull Location location, boolean center) {
        if (!this.isOnline()) return false;
        Location target = center ? location.getBlock().getLocation().clone().add(.5, 0, .5) : location;
        this.getPlayer().teleport(target, PlayerTeleportEvent.TeleportCause.UNKNOWN);
        return true;
    }

    @Override
    public boolean teleportToSync(@NotNull Plugin plugin, @NotNull MmcPlayer target) {
        if (!this.isOnline()) return false;
        Bukkit.getScheduler().runTask(plugin, () -> this.teleportTo(target));
        return true;
    }

    @Override
    public boolean teleportRelativeSync(@NotNull Plugin plugin, @NotNull RelativeLocation location) {
        return this.teleportRelativeSync(plugin, location, false);
    }

    @Override
    public boolean teleportRelativeSync(@NotNull Plugin plugin, @NotNull RelativeLocation location, boolean center) {
        if (!this.isOnline()) return false;
        Bukkit.getScheduler().runTask(plugin, () -> this.teleportRelative(location, center));
        return true;
    }

    @Override
    public boolean teleportSync(@NotNull Plugin plugin, @NotNull Location location) {
        return this.teleportSync(plugin, location, false);
    }

    @Override
    public boolean teleportSync(@NotNull Plugin plugin, @NotNull Location location, boolean center) {
        if (!this.isOnline()) return false;
        Bukkit.getScheduler().runTask(plugin, () -> this.teleport(location, center));
        return true;
    }

    public boolean sendMessage(@NotNull Component component) {
        if (!this.isOnline()) return false;
        this.getPlayer().sendMessage(component);
        return true;
    }

    public boolean sendActionBar(@NotNull Component component) {
        if (!this.isOnline()) return false;
        this.getPlayer().sendActionBar(component);
        return true;
    }

    public boolean sendTitle(@Nullable Component title, @Nullable Component subtitle) {
        return this.sendTitle(title, subtitle, Duration.ofSeconds(1L));
    }

    public boolean sendTitle(@Nullable Component title, @Nullable Component subtitle, @NotNull Duration stay) {
        return this.sendTitle(title, subtitle, Duration.ZERO, stay, Duration.ZERO);
    }

    public boolean sendTitle(@Nullable Component title, @Nullable Component subtitle, @NotNull Duration fade, @NotNull Duration stay) {
        return this.sendTitle(title, subtitle, fade, stay, fade);
    }

    public boolean sendTitle(@Nullable Component title, @Nullable Component subtitle, @NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
        return this.sendTitle(title, subtitle, Title.Times.times(fadeIn, stay, fadeOut));
    }

    private boolean sendTitle(@Nullable Component title, @Nullable Component subtitle, @Nullable Title.Times times) {
        if (!this.isOnline()) return false;
        this.getPlayer().showTitle(Title.title(!Objects.isNull(title) ? title : new ComponentBuilder().build(), !Objects.isNull(subtitle) ? subtitle : new ComponentBuilder().build(), times));
        return true;
    }

    /**
     * Clears the player's inventory and/or armor
     * @param clearMethod The {@link ClearMethod} to use to clear the player's inventory
     * @return true if the player was online and the inventory was cleared
     */
    public boolean clear(@Nullable ClearMethod clearMethod){
        switch (clearMethod){
            case INVENTORY -> {
                return this.clearInventory();
            }
            case ARMOR -> {
                return this.clearArmor();
            }
            default -> {
                return this.clear();
            }
        }
    }

    private boolean clear() {
        if (!this.isOnline()) return false;
        this.getInventory().clear();
        this.getPlayer().updateInventory();
        return true;
    }

    private boolean clearInventory() {
        if (!this.isOnline()) return false;
        this.getInventory().setStorageContents(new ItemStack[9*4]);
        this.getPlayer().updateInventory();
        return true;
    }

    private boolean clearArmor() {
        if (!this.isOnline()) return false;
        this.getInventory().setArmorContents(new ItemStack[4]);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean setArmor(@NotNull ItemStack[] armorContent) {
        if (!this.isOnline()) return false;
        if (armorContent.length != 4) return false;
        this.getInventory().setArmorContents(armorContent);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean setHelmet(@NotNull ItemStack item) {
        if (!this.isOnline()) return false;
        this.getInventory().setHelmet(item);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean setChestPlate(@NotNull ItemStack item) {
        if (!this.isOnline()) return false;
        this.getInventory().setChestplate(item);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean setLeggings(@NotNull ItemStack item) {
        if (!this.isOnline()) return false;
        this.getInventory().setLeggings(item);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean setBoots(@NotNull ItemStack item) {
        if (!this.isOnline()) return false;
        this.getInventory().setBoots(item);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean giveItem(@NotNull ItemBuilder item) {
        if (!this.isOnline()) return false;
        this.getInventory().addItem(item.build());
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean giveItems(@NotNull ItemBuilder... items) {
        for (ItemBuilder item : items) {
            boolean result = this.giveItem(item);
            if (!result) return result;
        }
        return true;
    }

    public boolean setItem(@NotNull ItemBuilder item, int slot) {
        return this.setItem(item.build(), slot);
    }

    public boolean setItem(@NotNull ItemStack item, int slot) {
        if (!this.isOnline()) return false;
        if (slot >= 36) return false;
        this.getInventory().setItem(slot, item);
        this.getPlayer().updateInventory();
        return true;
    }

    public boolean setSpawnPoint(@NotNull Location location) {
        if (!this.isOnline()) return false;
        this.getPlayer().setBedSpawnLocation(location, true);
        return true;
    }

    public boolean setFoodLevel(int level) {
        if (!this.isOnline()) return false;
        this.getPlayer().setFoodLevel(level);
        return true;
    }

    public boolean setSaturation(int level) {
        if (!this.isOnline()) return false;
        this.getPlayer().setSaturation(level);
        return true;
    }

    public boolean setHealth(double health) {
        if (!this.isOnline()) return false;
        this.getPlayer().setHealth(health);
        return true;
    }

    public boolean setMaxHealth(double maxHealth) {
        if (!this.isOnline()) return false;
        this.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        return true;
    }

    public boolean feed() {
        return this.setFoodLevel(20);
    }

    public boolean heal() {
        return this.heal(true);
    }

    public boolean heal(boolean feed) {
        if (!this.isOnline()) return false;
        this.setHealth(this.getMaxHealth());
        if (feed) this.feed();
        return true;
    }

    // CHECKS \\
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MmcPlayer player){
            return player.getUUID().equals(this.uuid);
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
        return this.isOnline() ? this.getPlayer().getName() : Bukkit.getOfflinePlayer(this.uuid).getName();
    }

    @Nullable
    public Component getNickName() {
        return this.isOnline() ? this.getPlayer().displayName() : null;
    }

    @NotNull
    public PlayerSpeed getWalkSpeed() {
        return PlayerSpeed.fromWalkSpeed(this.getPlayer().getWalkSpeed()).orElse(PlayerSpeed.LEVEL_1);
    }

    @NotNull
    public PlayerSpeed getFlySpeed() {
        return PlayerSpeed.fromFlySpeed(this.getPlayer().getFlySpeed()).orElse(PlayerSpeed.LEVEL_1);
    }

    @Nullable
    public PlayerInventory getInventory() {
        return this.isOnline() ? this.getPlayer().getInventory() : null;
    }

    @Nullable
    public Location getLocation() {
        return this.isOnline() ? this.getPlayer().getLocation() : null;
    }

    @Nullable
    public World getWorld() {
        return this.isOnline() ? this.getLocation().getWorld() : null;
    }

    public double getHealth() {
        return this.isOnline() ? this.getPlayer().getHealth() : -1;
    }

    public double getMaxHealth() {
        return this.isOnline() ? this.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : -1;
    }

    public int getFoodLevel() {
        return this.isOnline() ? this.getPlayer().getFoodLevel() : -1;
    }
}
