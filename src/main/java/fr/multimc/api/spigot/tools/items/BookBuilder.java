package fr.multimc.api.spigot.tools.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BookBuilder extends ItemBuilder {
    /**
     *
     */
    public BookBuilder() {
        super(Material.WRITTEN_BOOK);
    }

    /**
     *
     * @param item
     */
    public BookBuilder(@Nonnull ItemStack item) {
        super(item);
    }

    /**
     *
     * @param amount
     */
    public BookBuilder(int amount) {
        super(Material.WRITTEN_BOOK, amount);
    }

    /**
     *
     * @param author
     * @return
     */
    public BookBuilder setAuthor(@Nullable String author) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.setAuthor(author);
        return (BookBuilder) this.applyMeta(meta);
    }

    /**
     *
     * @param pages
     * @return
     */
    @Deprecated
    public BookBuilder addPages(@Nonnull String... pages) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.addPage(pages);
        return (BookBuilder) this.applyMeta(meta);
    }

    /**
     *
     * @param page
     * @return
     */
    @Deprecated
    public BookBuilder addPage(@Nonnull String page) {
        return this.addPages(page);
    }

    /**
     *
     * @param generation
     * @return
     */
    public BookBuilder setGeneration(@Nullable BookMeta.Generation generation) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.setGeneration(generation);
        return (BookBuilder) this.applyMeta(meta);
    }

    /**
     *
     * @param title
     * @return
     */
    public BookBuilder setTitle(@Nullable String title) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.setTitle(title);
        return (BookBuilder) this.applyMeta(meta);
    }
}
