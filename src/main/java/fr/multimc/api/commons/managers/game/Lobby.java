package fr.multimc.api.commons.managers.game;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.commons.managers.worldmanagement.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;

@SuppressWarnings("unused")
public class Lobby {

    private final File schematicFile;
    private final CustomLocation spawnPoint;
    private final String worldName;

    public Lobby(File schematicFile, CustomLocation spawnPoint, String worldName) {
        this.schematicFile = schematicFile;
        this.spawnPoint = spawnPoint;
        this.worldName = worldName;
    }

    public void init(){
        SchematicManager schematicManager = new SchematicManager(schematicFile);
        Location schematicLocation = this.getPasteLocation();
        try {
            schematicManager.pasteSchematic(schematicLocation, false, false, false);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getPasteLocation(){
        return new Location(Bukkit.getWorld(worldName), 0, 100, 0);
    }

    public Location getSpawnPoint() {
        return this.spawnPoint.toAbsolute(new Location(Bukkit.getWorld(worldName), 0, 100, 0));
    }
}
