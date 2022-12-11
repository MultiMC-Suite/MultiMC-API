package fr.multimc.api.spigot.worlds.schematics;

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
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Schematic {
    private final Clipboard clipboard;
    private final File schematicFile;
    private final SchematicOptions options;

    public Schematic(@NotNull Plugin plugin, @NotNull String name, @NotNull SchematicOptions options) {
        File pluginFile = new File("schematics/" + name + ".schem");
        this.schematicFile = new File(plugin.getDataFolder() + "/" + pluginFile.getPath());
        this.options = options;
        if(!schematicFile.exists()) {
            plugin.saveResource(pluginFile.getPath(), false);
        }
        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Schematic(@NotNull File schematicFile, @NotNull SchematicOptions options) {
        this.schematicFile = schematicFile;
        this.options = options;
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

    public void paste() throws WorldEditException {
        this.paste(this.options);
    }

    public void paste(@NotNull SchematicOptions options) throws WorldEditException {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(options.getLocation().getWorld()))) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(this.clipboard);
            PasteBuilder pasteBuilder = clipboardHolder.createPaste(editSession)
                    .to(BlockVector3.at(options.getLocation().getX(), options.getLocation().getY(), options.getLocation().getZ()))
                    .ignoreAirBlocks(options.isIgnoreAir())
                    .copyEntities(options.isCopyEntity())
                    .copyBiomes(options.isCopyBiomes());
            Operation operation = pasteBuilder.build();
            Operations.complete(operation);
        }
    }

    public HashMap<Material, Integer> getBlockCount(){
        HashMap<Material, Integer> blockCount = new HashMap<>();
        for(BlockVector3 blockVector3: this.clipboard.getRegion()){
            Material material = BukkitAdapter.adapt(this.clipboard.getBlock(blockVector3).getBlockType());
            if(blockCount.containsKey(material)){
                blockCount.put(material, blockCount.get(material) + 1);
            }else{
                blockCount.put(material, 1);
            }
        }
        return blockCount;
    }

    public File getSchematicFile() throws NullPointerException {
        if (!this.schematicFile.exists()) throw new NullPointerException("Provied file doesn't exist!");
        return schematicFile;
    }

    public SchematicOptions getOptions() {
        return options;
    }
}
