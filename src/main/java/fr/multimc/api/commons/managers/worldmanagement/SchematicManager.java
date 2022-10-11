package fr.multimc.api.commons.managers.worldmanagement;

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
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SuppressWarnings("unused")
public class SchematicManager {

    private final Clipboard clipboard;

    public SchematicManager(File dataFolder, String schematicName){
        try {
            this.clipboard = this.loadSchematic(new File(String.format("%s/%s", dataFolder.getPath(), schematicName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Clipboard loadSchematic(File file) throws IOException {
        Clipboard localClipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            localClipboard = reader.read();
        }
        return localClipboard;
    }

    public void pasteSchematic(Location location, boolean ignoreAirBlocks, boolean copyEntities, boolean copyBiomes) throws WorldEditException {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(this.clipboard);
            PasteBuilder pasteBuilder = clipboardHolder.createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(ignoreAirBlocks)
                    .copyEntities(copyEntities)
                    .copyBiomes(copyBiomes);
            Operation operation = pasteBuilder.build();
            Operations.complete(operation);
        }
    }
}
