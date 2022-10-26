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

@SuppressWarnings("unused")
public class Schematic {
    private final Clipboard clipboard;
    private final String name;
    private final File schematicFile;

    public Schematic(@Nonnull Plugin plugin, @Nonnull String name) {
        this.name = name;
        File pluginFile = new File("schematics/" + name + ".schem");
        this.schematicFile = new File(plugin.getDataFolder() + "/" + pluginFile.getPath());
        if(!schematicFile.exists()) {
            plugin.saveResource(pluginFile.getPath(), false);
        }

        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Schematic(File schematicFile) {
        this.name = schematicFile.getName().replace(".schem", "");
        this.schematicFile = schematicFile;

        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Clipboard load() throws IOException {
        Clipboard localClipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(this.schematicFile);

        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(this.schematicFile))) {
            localClipboard = reader.read();
        }

        return localClipboard;
    }

    public void paste(@Nonnull SchematicOptions options) throws WorldEditException {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(options.location.getWorld()))) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(this.clipboard);
            PasteBuilder pasteBuilder = clipboardHolder.createPaste(editSession)
                    .to(BlockVector3.at(options.location.getX(), options.location.getY(), options.location.getZ()))
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

    public File getSchematicFile() throws NullPointerException {
        if (!this.schematicFile.exists()) throw new NullPointerException("Provied file doesn't exist!");
        return schematicFile;
    }
}
