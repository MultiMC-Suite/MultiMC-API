package fr.multimc.api.spigot.managers.schematics;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Schematic {
    private final Clipboard clipboard;
    private final Plugin plugin;
    private final String name;
    private final File file;

    public Schematic(@Nonnull Plugin plugin, @Nonnull String name) {
        this.plugin = plugin;
        this.name = name;
        this.file = new File(plugin.getDataFolder() + "/schematics/" + name + ".schem");
        this.saveDefaults(false);

        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Schematic(@Nonnull Plugin plugin, @Nonnull File file) {
        this.plugin = plugin;
        this.name = file.getName().replace(".schem", "");
        this.file = file;
        this.saveDefaults(false);

        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Schematic(@Nonnull Plugin plugin, @Nonnull String name, File file) {
        this.plugin = plugin;
        this.name = name;
        this.file = file;
        this.saveDefaults(false);

        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDefaults(boolean force) {
        this.plugin.saveResource("schematics/" + this.name + ".schem", force);
    }

    private Clipboard load() throws IOException {
        Clipboard localClipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(this.file);

        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(this.file))) {
            localClipboard = reader.read();
        }

        return localClipboard;
    }

    public void paste(@Nonnull SchematicOptions options) throws WorldEditException {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(options.LOCATION.getWorld()))) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(this.clipboard);
            PasteBuilder pasteBuilder = clipboardHolder.createPaste(editSession)
                    .to(BlockVector3.at(options.LOCATION.getX(), options.LOCATION.getY(), options.LOCATION.getZ()))
                    .ignoreAirBlocks(options.IGNORE_AIR)
                    .copyEntities(options.COPY_ENTITIES)
                    .copyBiomes(options.COPY_BIOMES);
            Operation operation = pasteBuilder.build();
            Operations.complete(operation);
        }
    }

    public String getName() {
        return name;
    }

    public File getFile() throws NullPointerException {
        if (!this.file.exists()) throw new NullPointerException("Provied file doesn't exist!");
        return file;
    }
}
