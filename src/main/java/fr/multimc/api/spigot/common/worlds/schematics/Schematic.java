package fr.multimc.api.spigot.common.worlds.schematics;

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
import java.util.Map;

@SuppressWarnings("unused")
public class Schematic {
    private final Clipboard clipboard;
    private final File schematicFile;
    private final SchematicOptions options;

    /**
     * Constructor for {@link Schematic} class
     * @param plugin the {@link Plugin}
     * @param name the name of the schematic
     * @param options the {@link SchematicOptions} of the schematic
     */
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

    /**
     * Constructor for {@link Schematic} class
     * @param schematicFile the {@link File} of the schematic
     * @param options the {@link SchematicOptions} of the {@link Schematic}
     */
    public Schematic(@NotNull File schematicFile, @NotNull SchematicOptions options) {
        this.schematicFile = schematicFile;
        this.options = options;
        try {
            this.clipboard = this.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the {@link Schematic}
     * @return the loaded {@link Clipboard}
     * @throws IOException if there is an issue reading the file
     */
    private Clipboard load() throws IOException {
        Clipboard localClipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(this.schematicFile);

        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(this.schematicFile))) {
            localClipboard = reader.read();
        }

        return localClipboard;
    }

    /**
     * Paste the schematic
     * @throws WorldEditException the world edit exception
     */
    public void paste() throws WorldEditException {
        this.paste(this.options);
    }

    /**
     * Paste the schematic
     * @param options the options of the schematic
     * @throws WorldEditException the world edit exception
     */
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

    /**
     * Get the block count of the schematic
     * @return a {@link java.util.Map} containing the block count for each {@link Material}
     */
    public Map<Material, Integer> getBlockCount(){
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

    // PUBLIC GETTERS
    public File getSchematicFile() throws NullPointerException {
        if (!this.schematicFile.exists()) throw new NullPointerException("Provided file doesn't exist!");
        return schematicFile;
    }
    public SchematicOptions getOptions() {
        return options;
    }
    public Clipboard getClipboard() {
        return clipboard;
    }
}
