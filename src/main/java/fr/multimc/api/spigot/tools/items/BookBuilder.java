package fr.multimc.api.spigot.tools.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Create easily a book.
 *
 * @author Loïc MAES
 * @version 1.0
 * @since 04/10/2022
 */
public class BookBuilder extends ItemBuilder {
    /**
     * Create an instance of BookBuilder based on the ItemBuilder.
     */
    public BookBuilder() {
        super(Material.WRITTEN_BOOK);
    }

    /**
     * Create an instance of BookBuilder based on the ItemBuilder with an existing item.
     *
     * @param item Item.
     */
    public BookBuilder(@Nonnull ItemStack item) {
        super(item);
    }

    /**
     * Create an instance of BookBuilder based on the ItemBuilder with a custom amount.
     *
     * @param amount Amount.
     */
    public BookBuilder(int amount) {
        super(Material.WRITTEN_BOOK, amount);
    }

    /**
     * Set the book author name.
     *
     * @param author Author name.
     * @return Current instance of the builder.
     */
    public BookBuilder setAuthor(@Nullable String author) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.setAuthor(author);
        return (BookBuilder) this.applyMeta(meta);
    }

    /**
     * Add pages to the book.
     *
     * @param pages Pages.
     * @return Current instance of the builder.
     */
    @Deprecated
    public BookBuilder addPages(@Nonnull String... pages) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.addPage(pages);
        return (BookBuilder) this.applyMeta(meta);
    }

    /**
     * Add pages to the book.
     *
     * @param page Pages.
     * @return Current instance of the builder.
     */
    @Deprecated
    public BookBuilder addPage(@Nonnull String page) {
        return this.addPages(page);
    }

    /**
     * Set the book generation.
     *
     * @param generation Generation.
     * @return Current instance of the builder.
     */
    public BookBuilder setGeneration(@Nullable BookMeta.Generation generation) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.setGeneration(generation);
        return (BookBuilder) this.applyMeta(meta);
    }

    /**
     * Set the book title.
     *
     * @param title Title.
     * @return Current instance of the builder.
     */
    public BookBuilder setTitle(@Nullable String title) {
        BookMeta meta = (BookMeta) this.getMeta();
        meta.setTitle(title);
        return (BookBuilder) this.applyMeta(meta);
    }
}
