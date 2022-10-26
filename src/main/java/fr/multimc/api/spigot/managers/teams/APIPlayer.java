package fr.multimc.api.spigot.managers.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class APIPlayer {

    private final UUID uuid;
    private final String name;

    public APIPlayer(@NotNull UUID uuid){
        this.uuid = uuid;
        this.name = this.fetchName();
    }

    public APIPlayer(@NotNull String name){
        this.name = name;
        this.uuid = this.fetchUUID();
    }

    public APIPlayer(@NotNull Player player){
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    private String fetchName(){
        return Bukkit.getOfflinePlayer(this.uuid).getName();
    }

    private UUID fetchUUID(){
        return Bukkit.getOfflinePlayer(this.name).getUniqueId();
    }

    public boolean isOnline(){
        return Bukkit.getOfflinePlayer(this.uuid).isOnline();
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof APIPlayer player){
            return player.getUUID() == this.uuid;
        }
        return false;
    }
}
